package src.main.java.com.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    private static List<String> tokens = new ArrayList<>();

    public static void tokenizar(String expresion) {
        tokens.clear();
        Pattern pattern = Pattern.compile("[()]|[<>+\\-*/']|[A-Za-z0-9]+");
        Matcher matcher = pattern.matcher(expresion);

        while (matcher.find()) {
            tokens.add(matcher.group());
        }
    }

    public static List<String> obtenerTokens() {
        return new ArrayList<>(tokens);
    }
}
