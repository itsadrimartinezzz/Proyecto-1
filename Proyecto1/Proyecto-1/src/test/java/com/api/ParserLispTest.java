package src.test.java.com.api;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Arrays;
import src.main.java.com.api.ParserLisp;

import static org.junit.jupiter.api.Assertions.*;

class ParserLispTest {

    @Test
    void testParseSimpleExpression() {
        List<String> tokens = List.of("(", "+", "1", "2", ")");
        Object result = ParserLisp.parse(tokens);

        assertTrue(result instanceof List);
        List<?> parsedList = (List<?>) result;
        assertEquals(3, parsedList.size());
        assertEquals("+", parsedList.get(0));
        assertEquals(1, parsedList.get(1));
        assertEquals(2, parsedList.get(2));
    }

    @Test
    void testParseNestedExpression() {
        List<String> tokens = List.of("(", "+", "1", "(", "*", "2", "3", ")", ")");
        Object result = ParserLisp.parse(tokens);

        assertTrue(result instanceof List);
        List<?> parsedList = (List<?>) result;
        assertEquals("+", parsedList.get(0));
        assertEquals(1, parsedList.get(1));
        assertTrue(parsedList.get(2) instanceof List);

        List<?> nestedList = (List<?>) parsedList.get(2);
        assertEquals("*", nestedList.get(0));
        assertEquals(2, nestedList.get(1));
        assertEquals(3, nestedList.get(2));
    }

    @Test
    void testParseUnbalancedParentheses() {
        List<String> tokens = List.of("(", "+", "1", "2");
        assertThrows(IllegalArgumentException.class, () -> ParserLisp.parse(tokens));
    }

    // ======= SUMA =======
    @Test
    void testSumaEnteros() {
        List<Object> expr = Arrays.asList("+", 2, 3, 5);
        assertEquals(10, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testSumaDecimales() {
        List<Object> expr = Arrays.asList("+", 2, 4, 2);
        assertEquals(8, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testSumaMixta() {
        List<Object> expr = Arrays.asList("+", 2, 3, 4);
        assertEquals(9, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testRestaDecimales() {
        List<Object> expr = Arrays.asList("-", 10, 3, 1);
        assertEquals(6, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testRestaMixta() {
        List<Object> expr = Arrays.asList("-", 10, 3, 2);
        assertEquals(5, ParserLisp.evaluarExpresion(expr));
    }


    @Test
    void testSumaUnOperando() {
        List<Object> expr = Arrays.asList("+", 10);
        assertEquals(10, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testSumaConNegativos() {
        List<Object> expr = Arrays.asList("+", 5, -3, 2);
        assertEquals(4, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testSumaConCero() {
        List<Object> expr = Arrays.asList("+", 0, 0, 0);
        assertEquals(0, ParserLisp.evaluarExpresion(expr));
    }

    // ======= RESTA =======
    @Test
    void testRestaEnteros() {
        List<Object> expr = Arrays.asList("-", 10, 3, 2);
        assertEquals(5, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testRestaUnOperando() {
        List<Object> expr = Arrays.asList("-", 10);
        assertEquals(10, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testRestaConNegativos() {
        List<Object> expr = Arrays.asList("-", 5, -3, 2);
        assertEquals(6, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testRestaConCero() {
        List<Object> expr = Arrays.asList("-", 10, 0, 5);
        assertEquals(5, ParserLisp.evaluarExpresion(expr));
    }

    // ======= MULTIPLICACIÓN =======
    @Test
    void testMultiplicacionEnteros() {
        List<Object> expr = Arrays.asList("*", 2, 3, 4);
        assertEquals(24, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testMultiplicacionConCero() {
        List<Object> expr = Arrays.asList("*", 5, 0, 2);
        assertEquals(0, ParserLisp.evaluarExpresion(expr));
    }

    // ======= DIVISIÓN =======
    @Test
    void testDivisionEntera() {
        List<Object> expr = Arrays.asList("/", 20, 2, 2);
        assertEquals(5, ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testDivisionPorCero() {
        List<Object> expr = Arrays.asList("/", 10, 0);
        assertEquals("x/0 no se puede dividir entre 0", ParserLisp.evaluarExpresion(expr));
    }

    @Test
    void testDivisionCeroEntreCero() {
        List<Object> expr = Arrays.asList("/", 0, 0);
        assertEquals("0/0 indefinido", ParserLisp.evaluarExpresion(expr));
    }

    // ======= ERRORES =======
    @Test
    void testSumaOperandoNoValido() {
        List<Object> expr = Arrays.asList("+", 2, "hola");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> ParserLisp.evaluarExpresion(expr));
        assertEquals("Operando no válido en suma", ex.getMessage());
    }

    @Test
    void testRestaOperandoNoValido() {
        List<Object> expr = Arrays.asList("-", 2, "hola");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> ParserLisp.evaluarExpresion(expr));
        assertEquals("Operando no válido en resta", ex.getMessage());
    }

    @Test
    void testMultiplicacionOperandoNoValido() {
        List<Object> expr = Arrays.asList("*", 2, "hola");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> ParserLisp.evaluarExpresion(expr));
        assertEquals("Operando no válido en multiplicación", ex.getMessage());
    }

    @Test
    void testDivisionOperandoNoValido() {
        List<Object> expr = Arrays.asList("/", 2, "hola");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> ParserLisp.evaluarExpresion(expr));
        assertEquals("Operando no válido en división", ex.getMessage());
    }

    // ======= Evaluar atómico =======
    @Test
    void testEvaluarValorAtomico() {
        assertEquals(42, ParserLisp.evaluar(42));
    }

    @Test
    void testEvaluarListaSimple() {
        List<Object> expr = Arrays.asList("+", 2, 2);
        assertEquals(4, ParserLisp.evaluar(expr));
    }
}