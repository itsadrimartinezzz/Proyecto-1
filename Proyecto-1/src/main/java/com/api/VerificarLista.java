package src.main.java.com.api;

import java.util.Stack;

public class VerificarLista {

    public static boolean verificarEstructuraLisp(String expresion) {
        Stack<Character> pila = new Stack<>();

        for (char c : expresion.toCharArray()) {
            if (c == '(') {
                pila.push(c);
            } else if (c == ')') {
                if (pila.isEmpty() || pila.pop() != '(') {
                    return false; // Paréntesis desbalanceados
                }
            }
        }

        return pila.isEmpty(); // Si la pila está vacía, los paréntesis están balanceados
    }
}