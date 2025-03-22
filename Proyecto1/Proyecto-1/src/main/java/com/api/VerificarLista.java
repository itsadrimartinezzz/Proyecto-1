package src.main.java.com.api;

import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 * Clase que verifica la validez de la estructura de una expresión en formato Lisp.
 */
public class VerificarLista {

    /**
     * Verifica si una expresión en formato Lisp tiene una estructura válida.
     * @param expresion La expresión en formato Lisp a verificar.
     * @return true si la estructura es válida, false en caso contrario.
     */
    public static boolean verificarEstructuraLisp(String expresion) {
        List<String> tokens = convertirALista(expresion);
        Stack<Integer> pilaOperandos = new Stack<>();
        Stack<String> pilaExpresiones = new Stack<>();

        if (tokens.isEmpty()) return false; // Expresión vacía no es válida

        int indice = 0;
        while (indice < tokens.size()) {
            String token = tokens.get(indice);

            if (token.equals("(")) {
                pilaExpresiones.push(token);
                pilaOperandos.push(0);
            } else if (token.equals(")")) {
                if (pilaExpresiones.isEmpty()) return false;
                pilaExpresiones.pop();
                int operandos = pilaOperandos.pop();
                if (operandos < 1) return false; // Cada expresión debe tener al menos un argumento
            } else if (esOperador(token) || esPalabraReservada(token)) {
                if (indice + 1 >= tokens.size() || tokens.get(indice + 1).equals(")")) return false;
            } else if (token.equals("'")) {
                if (indice + 1 >= tokens.size() || tokens.get(indice + 1).equals(")")) return false;
            } else if (token.equals("DEFUN")) {
                if (indice + 3 >= tokens.size() || !tokens.get(indice + 1).matches("[A-Za-z][-A-Za-z0-9]*") || !tokens.get(indice + 2).equals("(")) return false;
            } else if (token.equals("SETQ")) {
                if (indice + 2 >= tokens.size() || !esVariable(tokens.get(indice + 1)) || tokens.get(indice + 2).equals(")")) return false;
            } else if (token.equals("COND")) {
                if (indice + 1 >= tokens.size() || tokens.get(indice + 1).equals(")")) return false;
            } else {
                if (pilaOperandos.isEmpty()) return false;
                pilaOperandos.push(pilaOperandos.pop() + 1);
            }
            indice++;
        }
        return pilaExpresiones.isEmpty();
    }

    /**
     * Convierte una expresión en formato Lisp a una lista de tokens.
     * @param expresion La expresión en formato Lisp a convertir.
     * @return Una lista de tokens.
     */
    private static List<String> convertirALista(String expresion) {
        List<String> listaTokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("[()]|[<>+\\-*/']|<=|>=|=|[A-Za-z0-9-]+|[0-9]+\\.[0-9]+");
        Matcher matcher = pattern.matcher(expresion);

        while (matcher.find()) {
            listaTokens.add(matcher.group());
        }
        return listaTokens;
    }

    /**
     * Verifica si una cadena es un operador.
     * @param cadena La cadena a verificar.
     * @return true si es un operador, false en caso contrario.
     */
    private static boolean esOperador(String cadena) {
        return "+-*/=<><=>=".contains(cadena);
    }

    /**
     * Verifica si una cadena es una variable válida.
     * @param cadena La cadena a verificar.
     * @return true si es una variable válida, false en caso contrario.
     */
    private static boolean esVariable(String cadena) {
        return cadena.matches("[A-Za-z][-A-Za-z0-9]*");
    }

    /**
     * Verifica si una cadena es un número.
     * @param cadena La cadena a verificar.
     * @return true si es un número, false en caso contrario.
     */
    private static boolean esNumero(String cadena) {
        return cadena.matches("-?\\d+") || cadena.matches("-?\\d+\\.\\d+");
    }

    /**
     * Verifica si una cadena es una palabra reservada en Lisp.
     * @param cadena La cadena a verificar.
     * @return true si es una palabra reservada, false en caso contrario.
     */
    private static boolean esPalabraReservada(String cadena) {
        return cadena.matches("DEFUN|SETQ|COND|ATOM|LIST|EQUAL|<|>|<=|>=|=|T|NIL|IF");
    }
}