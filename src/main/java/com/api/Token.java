package src.main.java.com.api;

import java.util.ArrayList;
import java.util.List;

public class Token {

    public static List<String> obtenerTokens(String expresion) {
        List<String> tokens = new ArrayList<>();
        StringBuilder tokenActual = new StringBuilder();
        boolean dentroDeQuote = false;

        for (char c : expresion.toCharArray()) {
            if (c == '(' || c == ')' || c == '\'' || c == '+' || c == '-' || c == '*' || c == '/' || c == '<' || c == '>') {
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
                tokens.add(String.valueOf(c));
                if (c == '\'') {
                    dentroDeQuote = !dentroDeQuote;  // Maneja el QUOTE (') 
                }
            } else if (Character.isWhitespace(c) && !dentroDeQuote) {
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
            } else {
                tokenActual.append(c);
            }
        }

        if (tokenActual.length() > 0) {
            tokens.add(tokenActual.toString());
        }

        return tokens;
    }
}