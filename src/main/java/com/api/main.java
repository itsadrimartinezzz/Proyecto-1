package src.main.java.com.api;

import java.util.List;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String expresion;
        boolean expresionValida = false;

        do {
            System.out.println("Ingrese una expresión en Lisp:");
            expresion = scanner.nextLine();

            // Primero verificamos la expresión
            expresionValida = VerificarLista.verificarPostfix(expresion);

            if (!expresionValida) {
                System.out.println("\nLa expresión ingresada NO es válida. Intente nuevamente.\n");
            }

        } while (!expresionValida); // Repetir hasta que la expresión sea válida

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
