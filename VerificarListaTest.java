package src.test.java.com.api;

import org.junit.jupiter.api.Test;
import src.main.java.com.api.VerificarLista;

import static org.junit.jupiter.api.Assertions.*;

class VerificarListaTest {

    @Test
    void testVerificarEstructuraLispValid() {
        assertTrue(VerificarLista.verificarEstructuraLisp("(+ 1 2)"));
    }

    @Test
    void testVerificarEstructuraLispInvalidUnbalancedParentheses() {
        assertFalse(VerificarLista.verificarEstructuraLisp("(+ 1 2"));
    }

    @Test
    void testVerificarEstructuraLispInvalidEmptyExpression() {
        assertFalse(VerificarLista.verificarEstructuraLisp(""));
    }

    @Test
    void testVerificarEstructuraLispValidNestedExpression() {
        assertTrue(VerificarLista.verificarEstructuraLisp("(+ 1 (* 2 3))"));
    }


    @Test
    void testVerificarEstructuraLispInvalidMissingOperand() {
        assertFalse(VerificarLista.verificarEstructuraLisp("(+)")); // operador sin operandos
    }
    @Test
    void testVerificarSetqInvalido() {
        // Forma estructuralmente inválida: no hay segundo argumento para SETQ
        // También podemos usar un cierre incorrecto para forzar error
        assertFalse(VerificarLista.verificarEstructuraLisp("(SETQ x")); // falta paréntesis de cierre
    }
    @Test
    void testVerificarCondValido() {
        assertFalse(VerificarLista.verificarEstructuraLisp("(COND ((> x 5) 1) ((< x 3) 2))"));
    }

    @Test
    void testVerificarDefunInvalido() {
        assertFalse(VerificarLista.verificarEstructuraLisp("(DEFUN)"));
    }

    @Test
    void testVerificarDefunValido() {
        assertTrue(VerificarLista.verificarEstructuraLisp("(DEFUN suma (x y) (+ x y))"));
    }


    @Test
    void testVerificarSetqValido() {
        assertTrue(VerificarLista.verificarEstructuraLisp("(SETQ x 10)"));
    }

    @Test
    void testVerificarCondInvalido() {
        assertFalse(VerificarLista.verificarEstructuraLisp("(COND)"));
    }

    @Test
    void testVerificarComillaValida() {
        assertTrue(VerificarLista.verificarEstructuraLisp("'(1 2 3)"));
    }

    @Test
    void testVerificarComillaInvalida() {
        assertFalse(VerificarLista.verificarEstructuraLisp("' )"));
    }
}
