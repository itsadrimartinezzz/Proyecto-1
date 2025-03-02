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
                pilaOperandos.push(0);
            } else if (token.equals(")")) {
                if (pilaExpresiones.isEmpty()) return false;
                pilaExpresiones.pop();
                int operandos = pilaOperandos.pop();
                if (operandos < 1) return false; // Cada expresión debe tener al menos un argumento
            } else if (esOperador(token) || esPalabraReservada(token)) {
                if (i + 1 >= tokens.size() || tokens.get(i + 1).equals(")")) return false;
            } else if (token.equals("'")) {
                // QUOTE debe tener un valor después de él
                if (i + 1 >= tokens.size() || tokens.get(i + 1).equals(")")) return false;
            } else if (token.equals("DEFUN")) {
                if (i + 3 >= tokens.size() || !tokens.get(i + 1).matches("[A-Za-z]+") || !tokens.get(i + 2).equals("(")) return false;
            } else if (token.equals("SETQ")) {
                if (i + 2 >= tokens.size() || !esVariable(tokens.get(i + 1)) || tokens.get(i + 2).equals(")")) return false;
            } else if (token.equals("COND")) {
                if (i + 1 >= tokens.size() || tokens.get(i + 1).equals(")")) return false;
            } else {
                if (pilaOperandos.isEmpty()) return false;
                pilaOperandos.push(pilaOperandos.pop() + 1);
            }
            i++;
        }
        return pilaExpresiones.isEmpty();
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

    private static boolean esPalabraReservada(String str) {
        return str.matches("DEFUN|SETQ|COND|ATOM|LIST|EQUAL|<|>|T");
    }
}
