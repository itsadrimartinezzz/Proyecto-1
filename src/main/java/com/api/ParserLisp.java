package src.main.java.com.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParserLisp {

    public static Object parse(List<String> tokens) {
        Stack<List<Object>> stack = new Stack<>();
        stack.push(new ArrayList<>());

        for (String token : tokens) {
            if (token.equals("(")) {
                List<Object> newList = new ArrayList<>();
                stack.peek().add(newList);
                stack.push(newList);
            } else if (token.equals(")")) {
                if (stack.size() > 1) {
                    stack.pop();
                } else {
                    throw new IllegalArgumentException("Paréntesis desbalanceados");
                }
            } else {
                // Convertimos números, dejamos strings como identificadores
                if (token.matches("-?\\d+")) {
                    stack.peek().add(Integer.parseInt(token));
                } else if (token.matches("-?\\d+\\.\\d+")) {
                    stack.peek().add(Double.parseDouble(token));
                } else {
                    stack.peek().add(token);
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Paréntesis desbalanceados");
        }

        List<Object> result = stack.pop();
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Expresión vacía");
        }

        return result.get(0);
    }

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

    public static Object evaluar(Object expresion) {
        if (expresion instanceof List) {
            return evaluarExpresion((List<Object>) expresion);
        }
        return expresion;
    }

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