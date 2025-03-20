package src.test.java.com.api;

import org.junit.jupiter.api.Test;
import java.util.List;
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
}
