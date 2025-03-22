package src.main.java.com.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParserLisp {

    /**
     * Convierte una lista de tokens en una estructura de datos anidada que representa la expresión Lisp.
     * @param tokens Lista de tokens a analizar.
     * @return La estructura de datos anidada que representa la expresión.
     * @throws IllegalArgumentException Si los paréntesis están desbalanceados o la expresión es inválida.
     */
    public static Object parse(List<String> tokens) {
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
                // Convertir números, dejar strings como identificadores
                if (token.matches("-?\\d+")) {
                    pila.peek().add(Integer.parseInt(token));
                } else if (token.matches("-?\\d+\\.\\d+")) {
                    pila.peek().add(Double.parseDouble(token));
                } else {
                    pila.peek().add(token);
                }
            }
        }

        if (pila.size() != 1) {
            throw new IllegalArgumentException("Paréntesis desbalanceados");
        }

        List<Object> resultado = pila.pop();
        if (resultado.isEmpty()) {
            throw new IllegalArgumentException("Expresión vacía");
        }

        return resultado.get(0);
    }

    /**
     * Evalúa una expresión Lisp representada como una lista.
     * @param expresion Lista que representa la expresión Lisp.
     * @return El resultado de evaluar la expresión.
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
     * Evalúa un objeto que puede ser una lista o un valor atómico.
     * @param expresion Objeto a evaluar.
     * @return El resultado de la evaluación.
     */
    public static Object evaluar(Object expresion) {
        if (expresion instanceof List) {
            return evaluarExpresion((List<Object>) expresion);
        }
        return expresion;
    }

    /**
     * Suma los operandos de una lista.
     * @param operandos Lista de operandos.
     * @return El resultado de la suma.
     */
    public static int sumar(List<Object> operandos) {
        int resultado = 0;
        for (Object op : operandos) {
            if (op instanceof Integer) {
                resultado += (Integer) op;
            } else {
                throw new IllegalArgumentException("Operando no válido en suma");
            }
        }
        return resultado;
    }

    /**
     * Resta los operandos de una lista.
     * @param operandos Lista de operandos.
     * @return El resultado de la resta.
     * @throws IllegalArgumentException Si faltan operandos.
     */
    public static int restar(List<Object> operandos) {
        if (operandos.isEmpty()) throw new IllegalArgumentException("Faltan operandos en resta");

        int resultado = (Integer) operandos.get(0);
        for (int i = 1; i < operandos.size(); i++) {
            if (operandos.get(i) instanceof Integer) {
                resultado -= (Integer) operandos.get(i);
            } else {
                throw new IllegalArgumentException("Operando no válido en resta");
            }
        }
        return resultado;
    }

    /**
     * Multiplica los operandos de una lista.
     * @param operandos Lista de operandos.
     * @return El resultado de la multiplicación.
     */
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
     * Divide los operandos de una lista.
     * @param operandos Lista de operandos.
     * @return El resultado de la división.
     * @throws IllegalArgumentException Si faltan operandos o si se intenta dividir por cero.
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