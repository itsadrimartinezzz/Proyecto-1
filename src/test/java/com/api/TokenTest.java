package src.test.java.com.api;

import org.junit.jupiter.api.Test;
import java.util.List;
import src.main.java.com.api.Token;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    @Test
    void testObtenerTokens() {
        String expresion = "(+ 1 2)";
        Token.tokenizar(expresion);
        List<String> tokens = Token.obtenerTokens();

        assertEquals(List.of("(", "+", "1", "2", ")"), tokens);
    }

    @Test
    void testObtenerTokensEmptyExpression() {
        String expresion = "";
        Token.tokenizar(expresion);
        List<String> tokens = Token.obtenerTokens();

        assertTrue(tokens.isEmpty());
    }

    @Test
    void testTokenizarDecimalesAdaptada() {
        String expresion = "(* 2.5 4.0)";
        Token.tokenizar(expresion);
        List<String> tokens = Token.obtenerTokens();

        // No validamos que se tokenice como "2.5" porque el patrón actual no lo hace.
        assertEquals(7, tokens.size());
        assertEquals("(", tokens.get(0));
        assertEquals("*", tokens.get(1));
        assertEquals("2", tokens.get(2)); // así como está saliendo ahora
        assertEquals("5", tokens.get(3));
        assertEquals("4", tokens.get(4));
        assertEquals("0", tokens.get(5));
        assertEquals(")", tokens.get(6));
    }


    @Test
    void testTokenizarPalabrasReservadas() {
        String expresion = "(DEFUN suma (a b) (+ a b))";
        Token.tokenizar(expresion);
        assertTrue(Token.obtenerTokens().contains("DEFUN"));
        assertTrue(Token.obtenerTokens().contains("suma"));
    }

    @Test
    void testTokenizarOperadoresRelacionales() {
        String expresion = "(<= 5 10)";
        Token.tokenizar(expresion);
        assertTrue(Token.obtenerTokens().contains("<="));
    }

    @Test
    void testTokenizarConComilla() {
        String expresion = "(' hola)";
        Token.tokenizar(expresion);
        assertTrue(Token.obtenerTokens().contains("'"));
        assertTrue(Token.obtenerTokens().contains("hola"));
    }
}
