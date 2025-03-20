package src.main.java.com.api;

import java.util.List;
import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String expresion;
        boolean expresionValida;

        do {
            System.out.println("Ingrese una expresión en Lisp:");
            expresion = scanner.nextLine();

            expresionValida = VerificarLista.verificarEstructuraLisp(expresion);

            if (!expresionValida) {
                System.out.println("\nLa expresión ingresada NO es válida. Intente nuevamente.\n");
            }

        } while (!expresionValida);

        // Tokenizar y guardar en lista accesible
        Token.tokenizar(expresion);
        List<String> tokens = Token.obtenerTokens();

        // Parsear y obtener estructura
        Object estructura = ParserLisp.parse(tokens);

        System.out.println("\nEstructura generada:");
        System.out.println(estructura);

        try {
            Token.tokenizar(expresion);
            Object resultado = ParserLisp.evaluarExpresion((List<Object>) estructura);
            System.out.println("Resultado: " + resultado);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }
}
