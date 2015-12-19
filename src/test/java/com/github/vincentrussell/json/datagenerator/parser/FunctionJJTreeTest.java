package com.github.vincentrussell.json.datagenerator.parser;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FunctionJJTreeTest {

    @BeforeClass
    public static void beforeClass() {
        FunctionRegistry.getInstance().registerClass(TestConcat.class);
        FunctionRegistry.getInstance().registerClass(TestGender.class);
    }


    @Test
    public void testFunctionWithTwoStringsArgs() throws ParseException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(\"a\",\"b\")".getBytes()));
        String result = functionParser.Parse();
        assertEquals("ab",result);
    }

    @Test
    public void testFunctionWithTwoNumbersArgs() throws ParseException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(1,23)".getBytes()));
        String result = functionParser.Parse();
        assertEquals("123",result);
    }

    @Test
    public void testFunctionWithOneStringOneNumberArgs() throws ParseException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(\"a\",23)".getBytes()));
        String result = functionParser.Parse();
        assertEquals("a23",result);
    }

    @Test
    public void testFunctionWithOneStringOneFloatArgs() throws ParseException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(\"a\",21231.342342)".getBytes()));
        String result = functionParser.Parse();
        assertEquals("a21231.342342",result);
    }

    @Test
    public void testFunctionWithNoArgs() throws ParseException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-gender()".getBytes()));
        String result = functionParser.Parse();
        assertTrue("male".equals(result) || "female".equals(result));
    }

    @Test
    public void testFunctionWithArgsNested() throws ParseException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(\"a \",test-gender())".getBytes()));
        String result = functionParser.Parse();
        assertTrue("a male".equals(result) || "a female".equals(result));
    }

    @Test
    public void testFunctionWithArgsMultipleNested() throws ParseException {
        FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream("test-concat(test-concat(\"a \",test-gender()),\" is cool\")".getBytes()));
        String result = functionParser.Parse();
        assertTrue("a male is cool".equals(result) || "a female is cool".equals(result));
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


}
