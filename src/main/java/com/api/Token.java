package src.main.java.com.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    private static List<String> tokens = new ArrayList<>();

    /**
     * Tokeniza una expresión en formato Lisp, dividiéndola en una lista de tokens.
     * @param expresion La expresión en formato Lisp a tokenizar.
     */
    public static void tokenizar(String expresion) {
        tokens.clear();

        // Patrón para reconocer correctamente operadores y otros tokens
        Pattern pattern = Pattern.compile("<=|>=|[()]|[<>+\\-*/']|=|[A-Za-z0-9-]+|[0-9]+\\.[0-9]+");
        Matcher matcher = pattern.matcher(expresion);

        while (matcher.find()) {
            tokens.add(matcher.group());
        }
    }

    /**
     * Obtiene la lista de tokens generada por el método {@code tokenizar}.
     * @return Una lista de tokens.
     */
    public static List<String> obtenerTokens() {
        return new ArrayList<>(tokens);
    }
}