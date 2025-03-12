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
}
