package src.main.java.com.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParserLisp {
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
        if (!(operador instanceof String)) return expresion;

        List<Object> operandos = expresion.subList(1, expresion.size());
        operandos.replaceAll(ParserLisp::evaluar);

        switch ((String) operador) {
            case "+":
                return sumar(operandos);
            case "-":
                return restar(operandos);
            case "*":
                return multiplicar(operandos);
            case "/":
                return dividir(operandos);
            default:
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
        return expresion;
    }

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