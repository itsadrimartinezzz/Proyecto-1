package src.main.java.com.api;

import java.util.List;
import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String expresion;

        System.out.println("Intérprete Lisp en Java");
        System.out.println("Ingrese expresiones en Lisp o escriba 'exit' para salir.");

        while (true) {
            System.out.print("\n> ");
            expresion = scanner.nextLine().trim();

            if (expresion.equalsIgnoreCase("exit")) {
                System.out.println("Saliendo del intérprete...");
                break;
            }

            try {
                Token.tokenizar(expresion);
                List<String> tokens = Token.obtenerTokens();
                Object estructura = ParserLisp.parse(tokens);

                Object resultado = Evaluator.evaluate(estructura);
                System.out.println("Resultado: " + resultado);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
