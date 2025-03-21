package src.main.java.com.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    private static List<String> tokens = new ArrayList<>();

    public static void tokenizar(String expresion) {
        tokens.clear();

        // Mejorar el patrón para reconocer correctamente los operadores de comparación como un solo token
        Pattern pattern = Pattern.compile("<=|>=|[()]|[<>+\\-*/']|=|[A-Za-z0-9-]+|[0-9]+\\.[0-9]+");
        Matcher matcher = pattern.matcher(expresion);

        System.out.println("Tokenizando: " + expresion);

        while (matcher.find()) {
            String token = matcher.group();
            tokens.add(token);
            System.out.println("Token: " + token);
        }
    }

    public static List<String> obtenerTokens() {
        return new ArrayList<>(tokens);
    }
}