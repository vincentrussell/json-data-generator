package com.github.vincentrussell.json.datagenerator.parser;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.google.common.base.Charsets;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FunctionJJTreeTest {

    @BeforeClass
    public static void beforeClass() {
        FunctionRegistry.getInstance().registerClass(TestConcat.class);
        FunctionRegistry.getInstance().registerClass(TestGender.class);
        FunctionRegistry.getInstance().registerClass(TestRandomFloat.class);
        FunctionRegistry.getInstance().registerClass(TestRandomInteger.class);
    }


    @Test
    public void testFunctionWithTwoStringsArgs() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(\"a\",\"b\")".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        assertEquals("ab", result);
    }

    @Test
    public void testFunctionWithTwoNumbersArgs() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(1,23)".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        assertEquals("123", result);
    }

    @Test
    public void testFunctionWithOneStringOneNumberArgs() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(\"a\",23)".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        assertEquals("a23", result);
    }

    @Test
    public void testFunctionWithOneStringOneFloatArgs() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(\"a\",21231.342342)".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        assertEquals("a21231.342342", result);
    }

    @Test
    public void testFunctionWithNoArgs() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-gender()".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        assertTrue("male".equals(result) || "female".equals(result));
    }

    @Test
    public void testFunctionWithArgsNested() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(\"a \",test-gender())".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        assertTrue("a male".equals(result) || "a female".equals(result));
    }

    @Test
    public void testFunctionWithArgsMultipleNested() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(test-concat(\"a \",test-gender()),\" is cool\")".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        assertTrue("a male is cool".equals(result) || "a female is cool".equals(result));
    }

    @Test
    public void canParseFloatingPointNumbers() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-floating(1.232,2.543)".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        Float flo = Float.valueOf(result);
        assertTrue(flo >= 1.232 && flo <= 2.543);
    }

    @Test
    public void canParseNegativeFloatingPointNumbers() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-floating(-3.543,-2.543)".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        Float flo = Float.valueOf(result);
        assertTrue(flo >= -3.543 && flo <= -2.543);
    }

    @Test
    public void canParseNegativeIntegers() throws ParseException, InvocationTargetException, IllegalAccessException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-integer(-20,-10)".getBytes()), Charsets.UTF_8);
        String result = functionParser.Parse();
        Integer integer = Integer.valueOf(result);
        assertTrue(integer >= -20 && integer <= -10);
    }

    @Function(name = "test-concat")
    public static class TestConcat {
        @FunctionInvocation
        public String concat(String... objects) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object object : objects) {
                stringBuilder.append(object.toString());
            }
            return stringBuilder.toString();
        }
    }

    @Function(name = "test-gender")
    public static class TestGender {

        @FunctionInvocation
        public String gender() {
            return (Math.random() < 0.5) ? "male" : "female";
        }
    }

    @Function(name = {"test-float", "test-floating"})
    public static class TestRandomFloat {

        private static final Random RANDOM = new Random();

        private String getRandomFloat(Float min, Float max, String format) {
            float randomNumber = min + (max - min) * RANDOM.nextFloat();

            if (format != null) {
                return String.format(format, randomNumber);
            }
            return Float.toString(randomNumber);
        }

        @FunctionInvocation
        public String getRandomFloat(String min, String max) {
            return getRandomFloat(min, max, null);
        }

        @FunctionInvocation
        public String getRandomFloat(String min, String max, String format) {
            return getRandomFloat(Float.parseFloat(min), Float.parseFloat(max), format);
        }

    }

    @Function(name = "test-integer")
    public static class TestRandomInteger {

        private static final Random RANDOM = new Random();

        @FunctionInvocation
        public String getRandomInteger(String min, String max) {
            return getRandomInteger(Integer.parseInt(min), Integer.parseInt(max));
        }

        private String getRandomInteger(Integer min, Integer max) {
            int randomNumber = RANDOM.nextInt(max - min) + min;
            return Integer.toString(randomNumber);
        }
    }


}
