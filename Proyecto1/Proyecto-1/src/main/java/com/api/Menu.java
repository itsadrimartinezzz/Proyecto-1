package src.main.java.com.api;

import java.util.List;
import java.util.Scanner;

/**
 * Clase principal que implementa un intérprete de Lisp en Java.
 * Permite al usuario ingresar expresiones Lisp, las analiza y evalúa.
 */
public class Menu {

    /**
     * Método principal que ejecuta el intérprete de Lisp.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
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
                expresion = expresion.replaceAll("\\s+", " ").trim();
                // Tokeniza la expresión ingresada
                Token.tokenizar(expresion);
                List<String> tokens = Token.obtenerTokens();

                // Convierte los tokens en una estructura de datos
                Object estructura = ParserLisp.parse(tokens);

                // Evalúa la estructura y muestra el resultado
                Object resultado = Evaluator.evaluate(estructura);
                System.out.println("Resultado: " + resultado);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
