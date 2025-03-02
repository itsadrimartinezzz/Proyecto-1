package src.main.java.com.api;

import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class VerificarLista {

    public static boolean verificarEstructuraLisp(String expresion) {
        List<String> tokens = convertirALista(expresion);
        Stack<Integer> pilaOperandos = new Stack<>();
        Stack<String> pilaExpresiones = new Stack<>();

        if (tokens.isEmpty()) return false; // Expresión vacía no es válida

        int i = 0;
        while (i < tokens.size()) {
            String token = tokens.get(i);

            if (token.equals("(")) {
                pilaExpresiones.push(token);
                pilaOperandos.push(0); // Contador de operandos para esta subexpresión
            } else if (token.equals(")")) {
                if (pilaExpresiones.isEmpty()) return false; // Cierre sin apertura
                pilaExpresiones.pop();

                int operandos = pilaOperandos.pop();
                if (operandos < 2) return false; // Un operador debe tener al menos 2 operandos
            } else if (esOperador(token)) {
                if (i + 1 >= tokens.size() || tokens.get(i + 1).equals(")")) {
                    return false; // Un operador sin operandos no es válido
                }
            } else if (esVariable(token) || esNumero(token)) {
                if (pilaOperandos.isEmpty()) return false;
                pilaOperandos.push(pilaOperandos.pop() + 1); // Aumentar el contador de operandos
            }
            i++;
        }
        return pilaExpresiones.isEmpty(); // Paréntesis balanceados y estructura correcta
    }

    private static List<String> convertirALista(String expresion) {
        List<String> lista = new ArrayList<>();
        Pattern pattern = Pattern.compile("[()]|[<>+\\-*/']|[A-Za-z0-9]+");
        Matcher matcher = pattern.matcher(expresion);

        while (matcher.find()) {
            lista.add(matcher.group());
        }
        return lista;
    }

    private static boolean esOperador(String str) {
        return "+-*/".contains(str);
    }

    private static boolean esVariable(String str) {
        return str.matches("[A-Za-z]+");
    }

    private static boolean esNumero(String str) {
        return str.matches("\\d+");
    }
}
