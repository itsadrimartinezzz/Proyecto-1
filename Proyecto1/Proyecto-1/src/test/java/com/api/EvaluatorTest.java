package src.test.java.com.api;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static src.main.java.com.api.Evaluator.evaluate;

class EvaluatorTest {

    @Test
    void testSumaBasica() {
        Object resultado = evaluate(List.of("+", "1", "2", "3"));
        assertEquals(6, ((Number) resultado).intValue());
    }

    @Test
    void testDefunYFuncionPersonalizada() {
        evaluate(List.of("DEFUN", "cuadrado", List.of("n"), List.of("*", "n", "n")));
        Object resultado = evaluate(List.of("cuadrado", "4"));
        assertEquals(16, ((Number) resultado).intValue());
    }


    @Test
    void testRestaUnaria() {
        Object resultado = evaluate(List.of("-", 5));
        assertEquals(-5, resultado);
    }

    @Test
    void testMultiplicacionConDecimales() {
        Object resultado = evaluate(List.of("*", 2.0, 3));
        assertEquals(6.0, resultado);
    }

    @Test
    void testDivisionBasica() {
        Object resultado = evaluate(List.of("/", 10, 2));
        assertEquals(5.0, resultado);
    }

    @Test
    void testIfVerdadero() {
        Object resultado = evaluate(List.of("IF", List.of("=", 1, 1), 100, 200));
        assertEquals(100, resultado);
    }

    @Test
    void testIfFalso() {
        Object resultado = evaluate(List.of("IF", List.of("=", 1, 2), 100, 200));
        assertEquals(200, resultado);
    }

    @Test
    void testCondicionConOperadorSeparado() {
        Object resultado = evaluate(List.of("IF", List.of("<", "=", 2, 3), 10, 20));
        assertEquals(10, resultado);
    }

    @Test
    void testSetqYUsoDeVariable() {
        evaluate(List.of("SETQ", "x", 5));
        Object resultado = evaluate("x");
        assertEquals(5, resultado);
    }

    @Test
    void testCond() {
        Object resultado = evaluate(List.of("COND",
                List.of(List.of("=", 1, 2), "no"),
                List.of(List.of("=", 2, 2), "sí")
        ));
        assertEquals("sí", resultado);
    }

    @Test
    void testPredicadoAtom() {
        Object resultado = evaluate(List.of("ATOM", 42));
        assertEquals(true, resultado);
    }

    @Test
    void testPredicadoList() {
        Object resultado = evaluate(List.of("LIST", List.of(1, 2, 3)));
        assertEquals(true, resultado);
    }

    @Test
    void testPredicadoEqual() {
        Object resultado = evaluate(List.of("EQUAL", 5, 5));
        assertEquals(true, resultado);
    }

    @Test
    void testComparacionMayorIgual() {
        Object resultado = evaluate(List.of(">=", 5, 2));
        assertEquals(true, resultado);
    }

    @Test
    void testComparacionMenor() {
        Object resultado = evaluate(List.of("<", 3, 5));
        assertEquals(true, resultado);
    }

    @Test
    void testQuote() {
        Object resultado = evaluate(List.of("QUOTE", List.of("a", "b", "c")));
        assertEquals(List.of("a", "b", "c"), resultado);
    }
}
