package src.main.java.com.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ParserLisp {

    private static final Map<String, Object> variables = new HashMap<>();
    private static final Map<String, FuncDef> funcionesDefinidas = new HashMap<>();

    private static class FuncDef {
        List<String> parametros;
        Object cuerpo;

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
                pila.push(new ArrayList<>());
            } else if (token.equals(")")) {
                if (pila.size() > 1) {
                    List<Object> completa = pila.pop();
                    pila.peek().add(completa);
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

        return pila.pop().get(0);
    }

    /*
    public static Object evaluarExpresion(List<Object> expresion) {
        if (expresion.isEmpty()) {
            return expresion;
        }

        Object operador = expresion.get(0);
        if (!(operador instanceof String)) {
            return evaluar(expresion);
        }

        String op = (String) operador;

        List<Object> operandos = expresion.subList(1, expresion.size());
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
            case "DEFUN":
                return defun(operandos);
            case "SETQ":
                return setq(operandos);
            case "COND":
                return cond(operandos);
            case "ATOM":
                return atom(operandos);
            case "LIST":
                return list(operandos);
            case "EQUAL":
                return equal(operandos);
            case "<":
                return menorQue(operandos);
            case ">":
                return mayorQue(operandos);
            case "T":
                return "T";
            case "FACTORIAL":
                return factorial(operandos);
            default:
                if (funcionesDefinidas.containsKey(op)) {
                    return invocarFuncionUsuario(op, operandos);
                }
                return expresion;
        }
    }
    * */
    public static Object evaluarExpresion(List<Object> expresion) {
        if (expresion.isEmpty()) {
            throw new IllegalArgumentException("Expresión vacía.");
        }

        Object operador = expresion.get(0);
        if (!(operador instanceof String)) {
            return evaluar(expresion); // Evaluar valores directamente
        }

        String op = (String) operador;

        // Evaluar todos los operandos
        List<Object> operandos = expresion.subList(1, expresion.size());
        operandos.replaceAll(ParserLisp::evaluar);

        // Manejar operadores y funciones
        switch (op) {
            case "+":
                return sumar(operandos);
            case "-":
                return restar(operandos);
            case "*":
                return multiplicar(operandos);
            case "/":
                return dividir(operandos);
            case "DEFUN":
                return defun(operandos);
            case "SETQ":
                return setq(operandos);
            case "COND":
                return cond(operandos);
            case "ATOM":
                return atom(operandos);
            case "LIST":
                return list(operandos);
            case "EQUAL":
                return equal(operandos);
            case "<":
                return menorQue(operandos);
            case ">":
                return mayorQue(operandos);
            case "T":
                return "T";
            case "FACTORIAL":
                return calcularFactorial(operandos);
            default:
                // Si es una función definida por el usuario
                if (funcionesDefinidas.containsKey(op)) {
                    return invocarFuncionUsuario(op, operandos);
                }
                throw new IllegalArgumentException("Operador o función no reconocida: " + op);
        }
    }

    public static Object evaluar(Object expresion) {
        if (expresion instanceof List) {
            return evaluarExpresion((List<Object>) expresion);
        }
        if (expresion instanceof String && variables.containsKey(expresion)) {
            return variables.get(expresion);
        }
        return expresion;
    }

    private static Object defun(List<Object> operandos) {
        if (operandos.size() < 3) {
            throw new IllegalArgumentException("Uso de DEFUN inválido. Forma: (DEFUN nombre (params) (cuerpo))");
        }

        String nombreFuncion = (String) operandos.get(0);
        List<String> parametros = new ArrayList<>();
        for (Object p : (List<Object>) operandos.get(1)) {
            parametros.add((String) p);
        }
        Object cuerpo = operandos.get(2);

        funcionesDefinidas.put(nombreFuncion, new FuncDef(parametros, cuerpo));
        return nombreFuncion;
    }

    private static Object setq(List<Object> operandos) {
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("Uso de SETQ inválido. Forma: (SETQ var valor)");
        }

        String nombreVar = (String) operandos.get(0);
        Object valor = evaluar(operandos.get(1));

        variables.put(nombreVar, valor);
        return valor;
    }

    private static Object cond(List<Object> operandos) {
        for (Object o : operandos) {
            List<Object> par = (List<Object>) o;
            if (par.size() < 2) {
                continue;
            }

            Object condicion = evaluar(par.get(0));
            if (esVerdadero(condicion)) {
                return evaluar(par.get(1));
            }
        }
        return null; // NIL
    }

    private static boolean esVerdadero(Object valor) {
        return valor != null && !"NIL".equals(valor);
    }
    // Función para calcular factorial
    private static Object calcularFactorial(List<Object> operandos) {
        if (operandos.size() != 1) {
            throw new IllegalArgumentException("Uso incorrecto de factorial. Ejemplo: (FACTORIAL 5)");
        }
        int n = (int) operandos.get(0);
        if (n == 0) return 1;
        return n * (int) calcularFactorial(List.of(n - 1));
    }

    // Invocar funciones definidas por el usuario
    private static Object invocarFuncionUsuario(String nombreFuncion, List<Object> args) {
        FuncDef def = funcionesDefinidas.get(nombreFuncion);
        if (def == null) {
            throw new IllegalArgumentException("Función no definida: " + nombreFuncion);
        }

        if (args.size() != def.parametros.size()) {
            throw new IllegalArgumentException("Número de argumentos incorrecto para " + nombreFuncion);
        }

        Map<String, Object> backup = new HashMap<>(variables);

        try {
            // Asignar parámetros a sus valores
            for (int i = 0; i < args.size(); i++) {
                variables.put(def.parametros.get(i), args.get(i));
            }
            // Evaluar el cuerpo de la función
            return evaluar(def.cuerpo);
        } finally {
            // Restaurar variables globales
            variables.clear();
            variables.putAll(backup);
        }
    }
    /*
    private static Object invocarFuncionUsuario(String nombreFuncion, List<Object> args) {
        FuncDef def = funcionesDefinidas.get(nombreFuncion);
        if (def == null) {
            throw new IllegalArgumentException("Función no definida: " + nombreFuncion);
        }

        if (args.size() != def.parametros.size()) {
            throw new IllegalArgumentException("Número de argumentos incorrecto para " + nombreFuncion);
        }

        Map<String, Object> backup = new HashMap<>(variables);

        try {
            for (int i = 0; i < args.size(); i++) {
                variables.put(def.parametros.get(i), evaluar(args.get(i)));
            }

            return evaluar(def.cuerpo);
        } finally {
            variables.clear();
            variables.putAll(backup);
        }
    }
    * */

    // Operaciones aritméticas básicas
    private static Object sumar(List<Object> operandos) {
        double resultado = 0;
        for (Object op : operandos) {
            resultado += ((Number) evaluar(op)).doubleValue();
        }
        return resultado;
    }

    private static Object restar(List<Object> operandos) {
        double resultado = ((Number) evaluar(operandos.get(0))).doubleValue();
        for (int i = 1; i < operandos.size(); i++) {
            resultado -= ((Number) evaluar(operandos.get(i))).doubleValue();
        }
        return resultado;
    }

    private static Object multiplicar(List<Object> operandos) {
        double resultado = 1;
        for (Object op : operandos) {
            resultado *= ((Number) evaluar(op)).doubleValue();
        }
        return resultado;
    }

    private static Object dividir(List<Object> operandos) {
        double resultado = ((Number) evaluar(operandos.get(0))).doubleValue();
        for (int i = 1; i < operandos.size(); i++) {
            resultado /= ((Number) evaluar(operandos.get(i))).doubleValue();
        }
        return resultado;
    }

    // Funciones adicionales
    private static Object atom(List<Object> operandos) {
        if (operandos.isEmpty()) {
            throw new IllegalArgumentException("ATOM requiere un operando");
        }
        Object arg = evaluar(operandos.get(0));
        return (arg instanceof List) ? "NIL" : "T";
    }

    private static Object list(List<Object> operandos) {
        return new ArrayList<>(operandos);
    }

    private static Object equal(List<Object> operandos) {
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("EQUAL requiere dos operandos");
        }
        Object x = evaluar(operandos.get(0));
        Object y = evaluar(operandos.get(1));
        return (x.equals(y)) ? "T" : "NIL";
    }

    private static Object menorQue(List<Object> operandos) {
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("< requiere dos operandos");
        }
        double x = ((Number) evaluar(operandos.get(0))).doubleValue();
        double y = ((Number) evaluar(operandos.get(1))).doubleValue();
        return (x < y) ? "T" : "NIL";
    }

    private static Object mayorQue(List<Object> operandos) {
        if (operandos.size() < 2) {
            throw new IllegalArgumentException("> requiere dos operandos");
        }
        double x = ((Number) evaluar(operandos.get(0))).doubleValue();
        double y = ((Number) evaluar(operandos.get(1))).doubleValue();
        return (x > y) ? "T" : "NIL";
    }

    // Función factorial
    private static Object factorial(List<Object> operandos) {
        if (operandos.size() != 1) {
            throw new IllegalArgumentException("FACTORIAL requiere exactamente un operando");
        }
        int n = ((Number) evaluar(operandos.get(0))).intValue();
        return factorialRec(n);
    }

    private static int factorialRec(int n) {
        if (n == 0) {
            return 1;
        }
        return n * factorialRec(n - 1);
    }
}