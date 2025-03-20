package src.main.java.com.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ParserLisp {

    /**
     * Entorno global de variables (para SETQ).
     */
    private static final Map<String, Object> variables = new HashMap<>();

    /**
     * Entorno global de funciones definidas por el usuario (para DEFUN).
     * Se almacenará como:
     *    clave: nombre de la función
     *    valor: un objeto FuncDef que tiene la lista de parámetros y el cuerpo
     */
    private static final Map<String, FuncDef> funcionesDefinidas = new HashMap<>();

    /**
     * Clase interna para representar la definición de una función:
     *  - parámetros
     *  - cuerpo (la lista que representa la forma Lisp)
     */
    private static class FuncDef {
        List<String> parametros; // nombres de parámetros
        Object cuerpo;           // cuerpo como una expresión Lisp (List<Object> anidada)

        FuncDef(List<String> parametros, Object cuerpo) {
            this.parametros = parametros;
            this.cuerpo = cuerpo;
        }
    }

    public static Object parse(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Expresión vacía");
        }
        
        Stack<List<Object>> pila = new Stack<>();
        pila.push(new ArrayList<>());

        for (String token : tokens) {
            if (token.equals("(")) {
                List<Object> nuevaLista = new ArrayList<>();
                pila.peek().add(nuevaLista);
                pila.push(nuevaLista);
            } else if (token.equals(")")) {
                if (pila.size() > 1) {
                    pila.pop();
                } else {
                    throw new IllegalArgumentException("Paréntesis desbalanceados");
                }
            } else {
                pila.peek().add(token.matches("\\d+") ? Integer.parseInt(token) : token);
            }
        }

        if (pila.size() != 1) {
            throw new IllegalArgumentException("Paréntesis desbalanceados");
        }

        // Aquí corregimos la estructura devuelta para evitar la lista extra
        List<Object> resultado = pila.pop();
        return resultado.size() == 1 ? resultado.get(0) : resultado;
    }

    /**
     * Evalúa una operación matemática (+, -, *, /) con operandos que pueden ser valores
     * o expresiones anidadas.
     *
     * @param operador   El operador matemático.
     * @param argumentos Lista de operandos o expresiones anidadas.
     * @return El resultado de la operación.
     */
    public static Object evaluarExpresion(List<Object> expresion) {
        if (expresion.isEmpty()) return expresion;

        Object operador = expresion.get(0);
        if (!(operador instanceof String)) {
            // Podría ser una lista anidada o número, devolver tal cual o evaluar
            return evaluar(expresion);
        }

        // Convertimos a String para comparar
        String op = (String) operador;

        // Evaluamos los operandos (en la mayoría de los casos se necesitan evaluados)
        List<Object> operandos = expresion.subList(1, expresion.size());
        // Nota: en macros o formas especiales, podríamos cambiar la forma de evaluar.
        // Aquí, de forma sencilla, evaluaremos todos a priori,
        // excepto en ciertos casos especiales como (DEFUN ...), etc.
        // Para la mayoría, está bien evaluarlos:
        operandos.replaceAll(ParserLisp::evaluar);

        switch (op) {
            case "+":
                return sumar(operandos);
            case "-":
                return restar(operandos);
            case "*":
                return multiplicar(operandos);
            case "/":
                return dividir(operandos);

            // --------- A partir de aquí: NUEVA FUNCIONALIDAD ---------

            case "DEFUN":
                // Forma esperada: (DEFUN nombre (param1 param2 ...) (cuerpo...))
                return defun(operandos);

            case "SETQ":
                // Forma esperada: (SETQ variable valor)
                return setq(operandos);

            case "COND":
                // Forma esperada: (COND (cond1 expr1) (cond2 expr2) ... )
                return cond(operandos);

            case "ATOM":
                // Forma esperada: (ATOM x)
                return atom(operandos);

            case "LIST":
                // Forma esperada: (LIST arg1 arg2 ...)
                // Devuelve una lista con los valores dados
                return list(operandos);

            case "EQUAL":
                // Forma esperada: (EQUAL x y)
                return equal(operandos);

            case "<":
                // Forma esperada: (< x y)
                return menorQue(operandos);

            case ">":
                // Forma esperada: (> x y)
                return mayorQue(operandos);

            case "T":
                // Si el operador es "T" en sí, devolvemos "T".
                return "T";

            default:
                // Si no es un operador nativo ni palabra clave, puede ser llamada a función definida
                // por el usuario. Ejemplo: (mi-funcion 1 2)
                if (funcionesDefinidas.containsKey(op)) {
                    return invocarFuncionUsuario(op, operandos);
                }
                // Si no se encuentra, retornamos la expresión como está o un error
                return expresion;
        }
    }

    /**
     * Evalúa una expresión en formato Lisp.
     * Si la expresión es una lista, evalúa el operador y los operandos.
     * Si no, devuelve la expresión tal cual.
     *
     * @param expresion La expresión a evaluar.
     * @return El resultado de la evaluación.
     */
    public static Object evaluar(Object expresion) {
        if (expresion instanceof List) {
            return evaluarExpresion((List<Object>) expresion);
        }
        // Si es una variable, podría estar en nuestro mapa
        if (expresion instanceof String) {
            String str = (String) expresion;
            if (variables.containsKey(str)) {
                return variables.get(str);
            }
        }
        return expresion;
    }

    // --------------------- NUEVAS FUNCIONES PARA FORMAS ESPECIALES ---------------------

    /**
     * Define una función en el entorno global.
     * @param operandos Lista de operandos después de 'DEFUN'
     * @return El nombre de la función o un error si la forma es inválida.
     */
    private static Object defun(List<Object> operandos) {
        // Se espera:
        //   operandos.get(0) => nombre de la función (String)
        //   operandos.get(1) => lista de parámetros (List<String>)
        //   operandos.get(2) => cuerpo (List<Object>)
        if (operandos.size() < 3) {
            throw new IllegalArgumentException("Uso de DEFUN inválido. Forma esperada: (DEFUN nombre (params) (cuerpo))");
        }
        Object nombreObj = operandos.get(0);
        Object paramsObj = operandos.get(1);
        Object cuerpo = operandos.get(2);

        if (!(nombreObj instanceof String)) {
            throw new IllegalArgumentException("El nombre de la función debe ser un símbolo (String).");
        }
        String nombreFuncion = (String) nombreObj;

        if (!(paramsObj instanceof List)) {
            throw new IllegalArgumentException("La lista de parámetros debe ser una lista.");
        }
        List<Object> listaParametros = (List<Object>) paramsObj;
        List<String> parametros = new ArrayList<>();
        for (Object p : listaParametros) {
            if (!(p instanceof String)) {
                throw new IllegalArgumentException("Los parámetros deben ser símbolos (String). Encontrado: " + p);
            }
            parametros.add((String) p);
        }

        // Se crea la definición de función y se almacena
        FuncDef funcDef = new FuncDef(parametros, cuerpo);
        funcionesDefinidas.put(nombreFuncion, funcDef);

        // Devolvemos algo indicativo, por ejemplo el nombre
        return nombreFuncion;
    }

    /**
     * Asigna un valor a una variable en el entorno global.
     * @param operandos Lista de operandos después de 'SETQ'
     * @return El valor asignado o un error si la forma es inválida.
     */
    private static Object setq(List<Object> operandos) {
        // Se espera: (SETQ variable valor)
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("Uso de SETQ inválido. Forma esperada: (SETQ var valor)");
        }
        Object varObj = operandos.get(0);
        Object valorObj = operandos.get(1);

        if (!(varObj instanceof String)) {
            throw new IllegalArgumentException("El nombre de la variable debe ser un símbolo (String).");
        }
        String nombreVar = (String) varObj;

        // Guardamos en el mapa global
        variables.put(nombreVar, valorObj);
        return valorObj;
    }

    /**
     * Evaluación de 'COND':
     * Se espera algo como: (COND (cond1 expr1) (cond2 expr2) ... )
     * Retorna la primera expr cuyo cond sea distinto de nil.
     */
    private static Object cond(List<Object> operandos) {
        // Cada elemento de operandos debería ser una lista de la forma (cond expr)
        for (Object o : operandos) {
            if (!(o instanceof List)) {
                continue;
            }
            List<Object> par = (List<Object>) o;
            if (par.size() < 2) {
                continue;
            }
            Object condicion = evaluar(par.get(0));
            if (esVerdadero(condicion)) {
                // Retornamos la evaluación de la expresión
                return evaluar(par.get(1));
            }
        }
        return null; // Si ninguna condición se cumple, retornamos null (equivalente a NIL)
    }

    /**
     * Verifica si algo es 'verdadero' para este Lisp simplificado.
     * Asumimos que 'NIL' o null es falso, todo lo demás es verdadero.
     */
    private static boolean esVerdadero(Object valor) {
        if (valor == null) return false;
        if ("NIL".equals(valor)) return false;
        return true;
    }

    /**
     * (ATOM x): retorna T si x no es una lista, de lo contrario NIL.
     */
    private static Object atom(List<Object> operandos) {
        if (operandos.isEmpty()) {
            throw new IllegalArgumentException("ATOM requiere un operando");
        }
        Object arg = operandos.get(0);
        // No reevaluamos 'arg' acá porque en evaluarExpresion ya se hizo.
        if (arg instanceof List) {
            return "NIL";
        } else {
            return "T";
        }
    }

    /**
     * (LIST x1 x2 x3 ...): retorna una lista con todos los operandos.
     */
    private static Object list(List<Object> operandos) {
        // Simplemente retornamos una nueva lista con los operandos ya evaluados.
        return new ArrayList<>(operandos);
    }

    /**
     * (EQUAL x y): compara si x e y son "iguales".
     * Para simplicidad, si x == y en Java o x.equals(y) (en caso de no nulo).
     */
    private static Object equal(List<Object> operandos) {
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("EQUAL requiere dos operandos");
        }
        Object x = operandos.get(0);
        Object y = operandos.get(1);
        // Comparación simple
        if (x == null && y == null) {
            return "T";
        }
        if (x != null && x.equals(y)) {
            return "T";
        }
        return "NIL";
    }

    /**
     * (< x y): compara si x < y numéricamente.
     * Si no son números o no hay 2 operandos, lanza excepción.
     */
    private static Object menorQue(List<Object> operandos) {
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("< requiere dos operandos");
        }
        Object x = operandos.get(0);
        Object y = operandos.get(1);
        if (!(x instanceof Number) || !(y instanceof Number)) {
            throw new IllegalArgumentException("Los operandos de < deben ser números");
        }
        double dx = ((Number) x).doubleValue();
        double dy = ((Number) y).doubleValue();
        return dx < dy ? "T" : "NIL";
    }

    /**
     * (> x y): compara si x > y numéricamente.
     * Si no son números o no hay 2 operandos, lanza excepción.
     */
    private static Object mayorQue(List<Object> operandos) {
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("> requiere dos operandos");
        }
        Object x = operandos.get(0);
        Object y = operandos.get(1);
        if (!(x instanceof Number) || !(y instanceof Number)) {
            throw new IllegalArgumentException("Los operandos de > deben ser números");
        }
        double dx = ((Number) x).doubleValue();
        double dy = ((Number) y).doubleValue();
        return dx > dy ? "T" : "NIL";
    }

    /**
     * Invoca una función definida por el usuario.
     * @param nombreFuncion Nombre de la función.
     * @param args Lista de argumentos ya evaluados.
     * @return El resultado de la función.
     */
    private static Object invocarFuncionUsuario(String nombreFuncion, List<Object> args) {
        FuncDef def = funcionesDefinidas.get(nombreFuncion);
        if (def == null) {
            throw new IllegalArgumentException("Función no definida: " + nombreFuncion);
        }

        // Verificamos número de parámetros
        if (args.size() != def.parametros.size()) {
            throw new IllegalArgumentException("Número de argumentos no coincide con la definición de la función " 
                                               + nombreFuncion);
        }

        // Guardar temporalmente variables originales
        Map<String, Object> backup = new HashMap<>(variables);

        try {
            // Asignamos cada parámetro en el entorno global
            // (para un verdadero Lisp deberíamos usar entornos anidados,
            // pero aquí se simplifica al entorno global)
            for (int i = 0; i < args.size(); i++) {
                String paramName = def.parametros.get(i);
                variables.put(paramName, args.get(i));
            }

            // Evaluamos el cuerpo en este entorno
            return evaluar(def.cuerpo);
        } finally {
            // Restauramos las variables originales para no afectar llamadas subsecuentes
            variables.clear();
            variables.putAll(backup);
        }
    }

    // --------------------- OPERACIONES ARITMÉTICAS ORIGINALES ---------------------

    public static Object sumar(List<Object> operandos) {
        double resultado = 0;
        for (Object op : operandos) {
            if (op instanceof Integer) {
                resultado += (Integer) op;
            } else if (op instanceof Double) {
                resultado += (Double) op;
            } else {
                throw new IllegalArgumentException("Operando no válido en suma: " + op);
            }
        }
        // Si todos los operandos son enteros, devuelve un entero
        if (operandos.stream().allMatch(op -> op instanceof Integer)) {
            return (int) resultado;
        }
        return resultado;
    }

    public static Object restar(List<Object> operandos) {
        if (operandos.isEmpty()) throw new IllegalArgumentException("Faltan operandos en resta");
        
        double resultado;
        if (operandos.get(0) instanceof Integer) {
            resultado = (Integer) operandos.get(0);
        } else if (operandos.get(0) instanceof Double) {
            resultado = (Double) operandos.get(0);
        } else {
            throw new IllegalArgumentException("Operando no válido en resta: " + operandos.get(0));
        }

        for (int i = 1; i < operandos.size(); i++) {
            if (operandos.get(i) instanceof Integer) {
                resultado -= (Integer) operandos.get(i);
            } else if (operandos.get(i) instanceof Double) {
                resultado -= (Double) operandos.get(i);
            } else {
                throw new IllegalArgumentException("Operando no válido en resta: " + operandos.get(i));
            }
        }

        // Si todos los operandos son enteros, devuelve un entero
        if (operandos.stream().allMatch(op -> op instanceof Integer)) {
            return (int) resultado;
        }
        return resultado;
    }

    public static int multiplicar(List<Object> operandos) {
        int resultado = 1;
        for (Object op : operandos) {
            if (op instanceof Integer) {
                resultado *= (Integer) op;
            } else {
                throw new IllegalArgumentException("Operando no válido en multiplicación");
            }
        }
        return resultado;
    }

    /**
     * Evalúa una división asegurándose de manejar el caso de división por cero.
     *
     * @param argumentos Lista de operandos.
     * @return El resultado de la división o un mensaje de error si hay división por cero.
     */
    public static Object dividir(List<Object> operandos) {
        if (operandos.isEmpty()) throw new IllegalArgumentException("Faltan operandos en división");
        
        int resultado = (Integer) operandos.get(0);
        for (int i = 1; i < operandos.size(); i++) {
            if (operandos.get(i) instanceof Integer) {
                int divisor = (Integer) operandos.get(i);
                if (divisor == 0) {
                    return resultado == 0 ? "0/0 indefinido" : "x/0 no se puede dividir entre 0";
                }
                resultado /= divisor;
            } else {
                throw new IllegalArgumentException("Operando no válido en división");
            }
        }
        return resultado;
    }
}
