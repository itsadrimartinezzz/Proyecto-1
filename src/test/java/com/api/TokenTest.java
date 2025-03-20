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

        assertEquals(5, tokens.size());
        assertEquals("(", tokens.get(0));
        assertEquals("+", tokens.get(1));
        assertEquals("1", tokens.get(2));
        assertEquals("2", tokens.get(3));
        assertEquals(")", tokens.get(4));
    }

    @Test
    void testObtenerTokensEmptyExpression() {
        String expresion = "";
        Token.tokenizar(expresion);
        List<String> tokens = Token.obtenerTokens();

        assertTrue(tokens.isEmpty());
    }
}
