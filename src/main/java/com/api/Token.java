package src.main.java.com.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {

    public static List<String> obtenerTokens(String expresion) {
        List<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("[()]|[<>+\\-*/']|[A-Za-z0-9]+");
        Matcher matcher = pattern.matcher(expresion);

        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }
}
