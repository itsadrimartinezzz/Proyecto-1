import java.util.ArrayList;

public class VerificarLista {
    
    public static boolean verificarPostfix(String expresion) {
        ArrayList<Object> tokens = convertirALista(expresion);
        return verificarPostfixLista(tokens);
    }    
    private static ArrayList<Object> convertirALista(String expresion) {
        ArrayList<Object> lista = new ArrayList<>();
        String[] tokens = expresion.trim().split("\\s+"); // Divide por espacios

        for (String token : tokens) {
            if (esNumero(token)) {
                lista.add(Double.parseDouble(token)); // Convertir a número
            } else if (token.equals("*") || token.equals("/")) {
                lista.add(token); // Mantener operadores como Strings
            } else {
                throw new IllegalArgumentException("Token inválido: " + token);
            }
        }
        return lista;
    }
    private static boolean esNumero(String str) {
        try {
            Double.parseDouble(str); // Intentar convertir a número
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private static boolean verificarPostfixLista(ArrayList<Object> expr) {
        int operandos = 0;

        for (Object token : expr) {
            if (token instanceof Number) {
                operandos++; // Un número se suma como posible operando
            } else if (token instanceof String) {
                String operador = (String) token;

                if (operador.equals("*") || operador.equals("/")) {
                    if (operandos < 2) return false; // Deben haber al menos 2 operandos antes de un operador
                    operandos--; // Una operación consume dos operandos y devuelve uno
                } else {
                    return false; // Token inválido en la expresión
                }
            } else {
                return false; // Elemento desconocido en la expresión
            }
        }
        return operandos == 1; // Debe quedar exactamente un resultado final
    }

}

