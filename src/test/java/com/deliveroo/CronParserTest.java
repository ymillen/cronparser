package com.deliveroo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CronParserTest {
    CronParser cronParser = new CronParser();

    @BeforeEach
    void setUp() {
    }

    @Test
    void testParseRawTimeUnit_InvalidExpression1() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cronParser.parseRawTimeUnit("3/1/3", 0, 6);
        });

        String expectedMessage = "Invalid expression";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testParseRawTimeUnit_InvalidExpression2() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cronParser.parseRawTimeUnit("3/*", 0, 6);
        });

        String expectedMessage = "Invalid expression";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testParseRawTimeUnit_InvalidExpression3() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cronParser.parseRawTimeUnit("-/6", 0, 6);
        });

        String expectedMessage = "Invalid expression";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testParseRawTimeUnit_InvalidExpression4() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cronParser.parseRawTimeUnit("*-14", 0, 6);
        });

        String expectedMessage = "Invalid expression";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testParseRawTimeUnit_InvalidValueInRange1() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cronParser.parseRawTimeUnit("41-6", 0, 6);
        });

        String expectedMessage = "Invalid value in range";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testParseRawTimeUnit_InvalidValueInRange2() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cronParser.parseRawTimeUnit("3-51/3", 0, 6);
        });

        String expectedMessage = "Invalid value in range";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testParseRawTimeUnit_InvalidValue() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            cronParser.parseRawTimeUnit("100000000", 1, 12);
        });

        String expectedMessage = "Invalid single value";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testParseRawTimeUnit_ShouldSucceed1() {
        String expectedString = "3 5 7 9 11";
        String generatedString = cronParser.parseRawTimeUnit("3/2", 1, 12);

        assertEquals(generatedString.compareTo(expectedString), 0);
    }

    @Test
    void testParseRawTimeUnit_ShouldSucceed2() {
        String expectedString = "0 1 2 3 4 5";
        String generatedString = cronParser.parseRawTimeUnit("*", 0, 5);

        assertEquals(generatedString.compareTo(expectedString), 0);
    }

    @Test
    void testParseRawTimeUnit_ShouldSucceed3() {
        String expectedString = "0 3 6";
        String generatedString = cronParser.parseRawTimeUnit("*/3", 0, 6);

        assertEquals(generatedString.compareTo(expectedString), 0);
    }

    @Test
    void testParseRawTimeUnit_ShouldSucceed4() {
        String expectedString = "5 20 35";
        String generatedString = cronParser.parseRawTimeUnit("5-45/15", -100, 100);

        assertEquals(generatedString.compareTo(expectedString), 0);
    }

    @Test
    void testParseRawTimeUnit_ShouldSucceed5() {
        String expectedString = "500";
        String generatedString = cronParser.parseRawTimeUnit("500", -100, 1000);

        assertEquals(generatedString.compareTo(expectedString), 0);
    }
}