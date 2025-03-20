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
        assertEquals(3, parsedList.size());
        assertEquals("+", parsedList.get(0));
        assertEquals(1, parsedList.get(1));
        assertTrue(parsedList.get(2) instanceof List);

        List<?> nestedList = (List<?>) parsedList.get(2);
        assertEquals(3, nestedList.size());
        assertEquals("*", nestedList.get(0));
        assertEquals(2, nestedList.get(1));
        assertEquals(3, nestedList.get(2));
    }

    @Test
    void testParseUnbalancedParentheses() {
        List<String> tokens = List.of("(", "+", "1", "2");
        assertThrows(IllegalArgumentException.class, () -> ParserLisp.parse(tokens));
    }

    // Pruebas para la suma
    @Test
    void testSumaEnteros() {
        List<Object> expresion = Arrays.asList("+", 2, 3, 5);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(10, resultado);
    }

    @Test
    void testSumaDecimales() {
        List<Object> expresion = Arrays.asList("+", 2.5, 3.7, 1.8);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(8.0, resultado);
    }

    @Test
    void testSumaMixta() {
        List<Object> expresion = Arrays.asList("+", 2, 3.5, 4);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(9.5, resultado);
    }

    @Test
    void testSumaUnOperando() {
        List<Object> expresion = Arrays.asList("+", 10);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(10, resultado);
    }

    @Test
    void testSumaConNegativos() {
        List<Object> expresion = Arrays.asList("+", 5, -3, 2);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(4, resultado);
    }

    @Test
    void testSumaConCero() {
        List<Object> expresion = Arrays.asList("+", 0, 0, 0);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(0, resultado);
    }

    @Test
    void testSumaOperandoNoValido() {
        List<Object> expresion = Arrays.asList("+", 2, "hola");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParserLisp.evaluarExpresion(expresion);
        });
        assertEquals("Operando no válido en suma: hola", exception.getMessage());
    }

    // Pruebas para la resta
    @Test
    void testRestaEnteros() {
        List<Object> expresion = Arrays.asList("-", 10, 3, 2);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(5, resultado);
    }

    @Test
    void testRestaDecimales() {
        List<Object> expresion = Arrays.asList("-", 10.5, 3.2, 1.3);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(6.0, resultado);
    }

    @Test
    void testRestaMixta() {
        List<Object> expresion = Arrays.asList("-", 10, 3.5, 2);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(4.5, resultado);
    }

    @Test
    void testRestaUnOperando() {
        List<Object> expresion = Arrays.asList("-", 10);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(10, resultado);
    }

    @Test
    void testRestaConNegativos() {
        List<Object> expresion = Arrays.asList("-", 5, -3, 2);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(6, resultado);
    }

    @Test
    void testRestaConCero() {
        List<Object> expresion = Arrays.asList("-", 10, 0, 5);
        Object resultado = ParserLisp.evaluarExpresion(expresion);
        assertEquals(5, resultado);
    }

    @Test
    void testRestaOperandoNoValido() {
        List<Object> expresion = Arrays.asList("-", 10, "hola");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParserLisp.evaluarExpresion(expresion);
        });
        assertEquals("Operando no válido en resta: hola", exception.getMessage());
    }
}