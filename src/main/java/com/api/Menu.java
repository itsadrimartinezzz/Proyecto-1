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

        // Si la expresión es válida, procedemos con la tokenización
        List<String> tokens = Token.obtenerTokens(expresion);
        System.out.println("\nLa expresión es válida.");
        System.out.println("\nTokens obtenidos:");
        for (String token : tokens) {
            System.out.println(token);
        }

        scanner.close();
    }
}
