package src.main.java.com.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    private static List<String> tokens = new ArrayList<>();

    // Validación de paréntesis balanceados (opcional, si lo deseas agregar en esta clase)
    public static boolean balanceParentesis(String expresion) {
        int contador = 0;
        for (char c : expresion.toCharArray()) {
            if (c == '(') contador++;
            else if (c == ')') contador--;
            if (contador < 0) return false; // Paréntesis cerrado antes de abrirse
        }
        return contador == 0; // Verifica que estén balanceados
    }

    public static void tokenizar(String expresion) {
        tokens.clear();

        // Reemplazar caracteres invisibles como espacios no estándar
        expresion = expresion.replace("\u00A0", " ");

        // Validar paréntesis balanceados
        if (!balanceParentesis(expresion)) {
            throw new IllegalArgumentException("Error: Paréntesis desbalanceados en la expresión.");
        }

        // Patrón para tokenizar expresiones
        Pattern pattern = Pattern.compile("\"[^\"]*\"|[()]|[<>+=\\-*/']|[-A-Za-z0-9]+");
        Matcher matcher = pattern.matcher(expresion);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
    }

    public static List<String> obtenerTokens() {
        return new ArrayList<>(tokens); // Proteger la encapsulación
    }
}
