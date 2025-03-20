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

    /**
     * Convierte una lista de tokens (strings) en una estructura interna (listas anidadas).
     */
    public static Object parse(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Expresión vacía");
        }

        Stack<List<Object>> pila = new Stack<>();
        pila.push(new ArrayList<>());

        for (String token : tokens) {
            if (token.equals("(")) {
                // Inicia una nueva sublista
                List<Object> nuevaLista = new ArrayList<>();
                pila.peek().add(nuevaLista);
                pila.push(nuevaLista);
            } else if (token.equals(")")) {
                // Termina una sublista
                if (pila.size() > 1) {
                    pila.pop();
                } else {
                    throw new IllegalArgumentException("Paréntesis desbalanceados");
                }
            } else {
                // Si el token es numérico (\d+), parse a Integer; si no, queda como String
                Object elemento = token.matches("\\d+") ? Integer.parseInt(token) : token;
                pila.peek().add(elemento);
            }
        }

        if (pila.size() != 1) {
            throw new IllegalArgumentException("Paréntesis desbalanceados");
        }

        // Quitamos la lista principal
        List<Object> resultado = pila.pop();
        // Si la lista resultante tiene 1 elemento, devolver ese elemento en vez de la lista
        return (resultado.size() == 1) ? resultado.get(0) : resultado;
    }

    /**
     * Evalúa una expresión Lisp representada como List<Object>.
     * Por convención, el primer elemento suele ser un operador o símbolo.
     */
    public static Object evaluarExpresion(List<Object> expresion) {
        if (expresion.isEmpty()) {
            return expresion;
        }

        // Tomamos el primer elemento como "operador"
        Object operador = expresion.get(0);
        if (!(operador instanceof String)) {
            // Si no es string, puede ser una lista anidada o un número
            // Lo tratamos como "evaluar la lista" directamente
            return evaluar(expresion);
        }

        String op = (String) operador;
        // El resto son operandos
        List<Object> operandos = expresion.subList(1, expresion.size());

        // Evaluamos los operandos en la mayoría de los casos
        operandos.replaceAll(ParserLisp::evaluar);

        // Dependiendo de 'op', hacemos distintas cosas
        switch (op) {
            // ---------------- Operaciones aritméticas ----------------
            case "+":
                return sumar(operandos);
            case "-":
                return restar(operandos);
            case "*":
                return multiplicar(operandos);
            case "/":
                return dividir(operandos);

            // ---------------- Comparadores ----------------
            case "<":
                return menorQue(operandos);
            case ">":
                return mayorQue(operandos);

            // NUEVO: <= (si quieres comparaciones <=)
            case "<=":
                return menorOIgual(operandos);

            // NUEVO: = (comparación numérica)
            case "=":
                return igualNumerico(operandos);

            // Uso de EQUAL para comparar objetos en general
            case "EQUAL":
                return equal(operandos);

            // ---------------- Formas especiales / macros ----------------
            case "DEFUN":
                return defun(operandos);
            case "SETQ":
                return setq(operandos);
            case "COND":
                return cond(operandos);

            // NUEVO: IF
            case "IF":
                return ifEspecial(operandos);

            case "ATOM":
                return atom(operandos);
            case "LIST":
                return list(operandos);

            // T se asume verdadero
            case "T":
                return "T";

            // ---------------- Llamada a función definida por usuario ----------------
            default:
                // Si 'op' está en el mapa de funcionesDefinidas, la invocamos
                if (funcionesDefinidas.containsKey(op)) {
                    return invocarFuncionUsuario(op, operandos);
                }
                // En caso contrario, devolvemos la expresión tal cual (no se reconoce)
                return expresion;
        }
    }

    /**
     * Evalúa un objeto que puede ser List, String (símbolo) o Integer.
     */
    public static Object evaluar(Object expresion) {
        if (expresion instanceof List) {
            return evaluarExpresion((List<Object>) expresion);
        }
        // Si es un string, puede ser una variable
        if (expresion instanceof String) {
            String simbolo = (String) expresion;
            if (variables.containsKey(simbolo)) {
                return variables.get(simbolo);
            }
        }
        // Si no es lista ni variable, devuélvelo tal cual
        return expresion;
    }

    // -------------- Implementaciones de operadores y formas especiales --------------

    // ---------- Comparador "=" (nuevo) ----------
    private static Object igualNumerico(List<Object> operandos) {
        // ( = x y )
        if (operandos.size() != 2) {
            throw new IllegalArgumentException("= requiere 2 operandos");
        }
        Object x = operandos.get(0);
        Object y = operandos.get(1);

        if (!(x instanceof Number) || !(y instanceof Number)) {
            throw new IllegalArgumentException("Los operandos de = deben ser números");
        }

        double dx = ((Number) x).doubleValue();
        double dy = ((Number) y).doubleValue();
        return (dx == dy) ? "T" : "NIL";
    }

    // ---------- Comparador "<=" (nuevo) ----------
    private static Object menorOIgual(List<Object> operandos) {
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("<= requiere 2 operandos");
        }
        Object x = operandos.get(0);
        Object y = operandos.get(1);
        if (!(x instanceof Number) || !(y instanceof Number)) {
            throw new IllegalArgumentException("Los operandos de <= deben ser números");
        }
        double dx = ((Number) x).doubleValue();
        double dy = ((Number) y).doubleValue();
        return (dx <= dy) ? "T" : "NIL";
    }

    // ---------- Forma especial IF (nuevo) ----------
    private static Object ifEspecial(List<Object> operandos) {
        // (IF condicion then [else])
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("IF requiere al menos 2 operandos (cond y then)");
        }
        Object cond = operandos.get(0);
        Object thenBranch = operandos.get(1);
        Object elseBranch = (operandos.size() > 2) ? operandos.get(2) : null;

        // si la condicion es verdadera
        if (esVerdadero(cond)) {
            return evaluar(thenBranch);
        } else {
            // si hay else, lo evaluamos
            return (elseBranch != null) ? evaluar(elseBranch) : null;
        }
    }

    // ---------- Forma especial DEFUN ----------
    private static Object defun(List<Object> operandos) {
        // (DEFUN nombre (param1 param2 ...) (cuerpo...))
        if (operandos.size() < 3) {
            throw new IllegalArgumentException("Uso de DEFUN inválido. Ej: (DEFUN nombre (params) (cuerpo))");
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

        // Creamos la definición de función y la guardamos en el mapa
        FuncDef def = new FuncDef(parametros, cuerpo);
        funcionesDefinidas.put(nombreFuncion, def);

        // Devolvemos el nombre de la función como confirmación
        return nombreFuncion;
    }

    // ---------- Forma especial SETQ ----------
    private static Object setq(List<Object> operandos) {
        // (SETQ var valor)
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("Uso de SETQ inválido. Ej: (SETQ var valor)");
        }
        Object varObj = operandos.get(0);
        Object valorObj = operandos.get(1);

        if (!(varObj instanceof String)) {
            throw new IllegalArgumentException("El nombre de la variable debe ser un símbolo (String).");
        }
        String nombreVar = (String) varObj;

        // Asigna en el mapa de variables global
        variables.put(nombreVar, valorObj);
        return valorObj;
    }

    // ---------- Forma especial COND ----------
    private static Object cond(List<Object> operandos) {
        // (COND (cond1 expr1) (cond2 expr2) ...)
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
                return evaluar(par.get(1));
            }
        }
        // Si ninguna condición es verdadera, retorna NIL (null)
        return null;
    }

    // Verifica si algo es verdadero en este mini Lisp
    private static boolean esVerdadero(Object valor) {
        if (valor == null) return false;
        if ("NIL".equals(valor)) return false;
        return true;
    }

    // ---------- Forma especial ATOM ----------
    private static Object atom(List<Object> operandos) {
        // (ATOM x)
        if (operandos.isEmpty()) {
            throw new IllegalArgumentException("ATOM requiere un operando");
        }
        Object arg = operandos.get(0);
        // Devuelve "T" si no es lista, "NIL" si es lista
        return (arg instanceof List) ? "NIL" : "T";
    }

    // ---------- Forma especial LIST ----------
    private static Object list(List<Object> operandos) {
        // (LIST x1 x2 x3 ...)
        return new ArrayList<>(operandos);
    }

    // ---------- Forma especial EQUAL ----------
    private static Object equal(List<Object> operandos) {
        // (EQUAL x y)
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("EQUAL requiere dos operandos");
        }
        Object x = operandos.get(0);
        Object y = operandos.get(1);

        if (x == null && y == null) {
            return "T";
        }
        if (x != null && x.equals(y)) {
            return "T";
        }
        return "NIL";
    }

    // ---------- Comparador < ----------
    private static Object menorQue(List<Object> operandos) {
        // (< x y)
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
        return (dx < dy) ? "T" : "NIL";
    }

    // ---------- Comparador > ----------
    private static Object mayorQue(List<Object> operandos) {
        // (> x y)
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
        return (dx > dy) ? "T" : "NIL";
    }

    /**
     * Invoca una función definida por el usuario, p.ej. (mi-funcion arg1 arg2).
     */
    private static Object invocarFuncionUsuario(String nombreFuncion, List<Object> args) {
        FuncDef def = funcionesDefinidas.get(nombreFuncion);
        if (def == null) {
            throw new IllegalArgumentException("Función no definida: " + nombreFuncion);
        }
        // Comprobamos aridad
        if (args.size() != def.parametros.size()) {
            throw new IllegalArgumentException(
                    "Número de argumentos no coincide con la definición de la función " + nombreFuncion
            );
        }

        // Resguardamos variables actuales
        Map<String, Object> backup = new HashMap<>(variables);

        try {
            // Asignamos cada parámetro en el entorno global (simplificado)
            for (int i = 0; i < args.size(); i++) {
                String paramName = def.parametros.get(i);
                variables.put(paramName, args.get(i));
            }
            // Evaluamos el cuerpo en este entorno
            return evaluar(def.cuerpo);
        } finally {
            // Restauramos las variables originales
            variables.clear();
            variables.putAll(backup);
        }
    }

    // ------------------ Operaciones aritméticas ------------------

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
        // Si todos son enteros, retornamos int
        if (operandos.stream().allMatch(op -> op instanceof Integer)) {
            return (int) resultado;
        }
        return resultado;
    }

    public static Object restar(List<Object> operandos) {
        if (operandos.isEmpty()) {
            throw new IllegalArgumentException("Faltan operandos en resta");
        }
        double resultado;
        Object primero = operandos.get(0);
        if (primero instanceof Integer) {
            resultado = (Integer) primero;
        } else if (primero instanceof Double) {
            resultado = (Double) primero;
        } else {
            throw new IllegalArgumentException("Operando no válido en resta: " + primero);
        }

        for (int i = 1; i < operandos.size(); i++) {
            Object op = operandos.get(i);
            if (op instanceof Integer) {
                resultado -= (Integer) op;
            } else if (op instanceof Double) {
                resultado -= (Double) op;
            } else {
                throw new IllegalArgumentException("Operando no válido en resta: " + op);
            }
        }

        // Si todos eran enteros, devolvemos int
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

    public static Object dividir(List<Object> operandos) {
        if (operandos.isEmpty()) {
            throw new IllegalArgumentException("Faltan operandos en división");
        }
        Object primero = operandos.get(0);
        if (!(primero instanceof Integer)) {
            throw new IllegalArgumentException("División solo soporta enteros (en este mini-Lisp)");
        }
        int resultado = (Integer) primero;

        for (int i = 1; i < operandos.size(); i++) {
            Object op = operandos.get(i);
            if (!(op instanceof Integer)) {
                throw new IllegalArgumentException("División solo soporta enteros");
            }
            int divisor = (Integer) op;
            if (divisor == 0) {
                // Manejo de /0
                return (resultado == 0)
                        ? "0/0 indefinido"
                        : "x/0 no se puede dividir entre 0";
            }
            resultado /= divisor;
        }
        return resultado;
    }
}
