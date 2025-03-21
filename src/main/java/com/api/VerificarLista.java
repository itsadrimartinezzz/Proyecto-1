package src.main.java.com.api;

import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

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

        int i = 0;
        while (i < tokens.size()) {
            String token = tokens.get(i);

            if (token.equals("(")) {
                // Inicia una nueva expresión
                pilaExpresiones.push(token);
                pilaOperandos.push(0);
            } else if (token.equals(")")) {
                // Termina una expresión
                if (pilaExpresiones.isEmpty()) return false;
                pilaExpresiones.pop();
                int operandos = pilaOperandos.pop();
                if (operandos < 1) return false; // Cada expresión debe tener al menos un argumento
            } else if (esOperador(token) || esPalabraReservada(token)) {
                // Operadores y palabras reservadas deben tener al menos un argumento
                if (i + 1 >= tokens.size() || tokens.get(i + 1).equals(")")) return false;
            } else if (token.equals("'")) {
                // QUOTE debe tener un valor después de él
                if (i + 1 >= tokens.size() || tokens.get(i + 1).equals(")")) return false;
            } else if (token.equals("DEFUN")) {
                // DEFUN debe tener un nombre de función y una lista de parámetros
                if (i + 3 >= tokens.size() || !tokens.get(i + 1).matches("[A-Za-z][-A-Za-z0-9]*") || !tokens.get(i + 2).equals("(")) return false;
            } else if (token.equals("SETQ")) {
                // SETQ debe tener una variable y un valor
                if (i + 2 >= tokens.size() || !esVariable(tokens.get(i + 1)) || tokens.get(i + 2).equals(")")) return false;
            } else if (token.equals("COND")) {
                // COND debe tener al menos una condición
                if (i + 1 >= tokens.size() || tokens.get(i + 1).equals(")")) return false;
            } else {
                // Contar operandos
                if (pilaOperandos.isEmpty()) return false;
                pilaOperandos.push(pilaOperandos.pop() + 1);
            }
            i++;
        }
        return pilaExpresiones.isEmpty();
    }

    /**
     * Convierte una expresión en formato Lisp a una lista de tokens.
     * @param expresion La expresión en formato Lisp a convertir.
     * @return Una lista de tokens.
     */
    private static List<String> convertirALista(String expresion) {
        List<String> lista = new ArrayList<>();
        Pattern pattern = Pattern.compile("[()]|[<>+\\-*/']|<=|>=|=|[A-Za-z0-9-]+|[0-9]+\\.[0-9]+");
        Matcher matcher = pattern.matcher(expresion);

        while (matcher.find()) {
            lista.add(matcher.group());
        }
        return lista;
    }

    /**
     * Verifica si una cadena es un operador.
     * @param str La cadena a verificar.
     * @return true si es un operador, false en caso contrario.
     */
    private static boolean esOperador(String str) {
        return "+-*/=<><=>=".contains(str);
    }

    /**
     * Verifica si una cadena es una variable válida.
     * @param str La cadena a verificar.
     * @return true si es una variable válida, false en caso contrario.
     */
    private static boolean esVariable(String str) {
        return str.matches("[A-Za-z][-A-Za-z0-9]*");
    }

    /**
     * Verifica si una cadena es un número.
     * @param str La cadena a verificar.
     * @return true si es un número, false en caso contrario.
     */
    private static boolean esNumero(String str) {
        return str.matches("-?\\d+") || str.matches("-?\\d+\\.\\d+");
    }

    /**
     * Verifica si una cadena es una palabra reservada en Lisp.
     * @param str La cadena a verificar.
     * @return true si es una palabra reservada, false en caso contrario.
     */
    private static boolean esPalabraReservada(String str) {
        return str.matches("DEFUN|SETQ|COND|ATOM|LIST|EQUAL|<|>|<=|>=|=|T|NIL|IF");
    }
}