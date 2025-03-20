package src.test.java.com.api;

import org.junit.jupiter.api.Test;
import src.main.java.com.api.VerificarLista;

import static org.junit.jupiter.api.Assertions.*;

class VerificarListaTest {

    @Test
    void testVerificarEstructuraLispValid() {
        String expresion = "(+ 1 2)";
        assertTrue(VerificarLista.verificarEstructuraLisp(expresion));
    }

    @Test
    void testVerificarEstructuraLispInvalidUnbalancedParentheses() {
        String expresion = "(+ 1 2";
        assertFalse(VerificarLista.verificarEstructuraLisp(expresion));
    }

    @Test
    void testVerificarEstructuraLispInvalidEmptyExpression() {
        String expresion = "";
        assertFalse(VerificarLista.verificarEstructuraLisp(expresion));
    }

    @Test
    void testVerificarEstructuraLispValidNestedExpression() {
        String expresion = "(+ 1 (* 2 3))";
        assertTrue(VerificarLista.verificarEstructuraLisp(expresion));
    }

    @Test
    void testVerificarEstructuraLispInvalidMissingOperand() {
        String expresion = "(+ 1 )";
        assertFalse(VerificarLista.verificarEstructuraLisp(expresion));
    }
}
