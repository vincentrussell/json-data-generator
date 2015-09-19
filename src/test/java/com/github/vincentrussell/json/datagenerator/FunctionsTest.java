package com.github.vincentrussell.json.datagenerator;

import com.github.vincentrussell.json.datagenerator.impl.FunctionTokenResolver;
import com.github.vincentrussell.json.datagenerator.impl.FunctionsImpl;
import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class FunctionsTest {

    private IndexHolder indexHolder;
    private FunctionsImpl functions = new FunctionsImpl(indexHolder);

    @Before
    public void resetIndexHolder() {
        indexHolder = new IndexHolder();
    }

    @Test
    public void isFunctionSimple() {
        assertTrue(functions.isFunction("{{hello()}}"));
    }

    @Test
    public void isFunction1Arg() {
        assertTrue(functions.isFunction("{{hello(1)}}"));
    }

    @Test
    public void nestedFunctionWithArgs() {
        assertThat(functions.getFunctionNameAndArguments("{{goodBye(hello(1))}}"), contains(
                equalTo(new Object[]{"hello", "1"}),
                equalTo(new Object[]{"goodBye", FunctionsImpl.NESTED_RESULT})
        ));
    }

    @Test
    public void functionAndArgNumber() {
        assertArrayEquals(new Object[]{"hello", "1"}, Iterables.getFirst(functions.getFunctionNameAndArguments("{{hello(1)}}"), null));
    }

    @Test
    public void functionAndArgText() {
        assertArrayEquals(new Object[]{"hello", "1"}, Iterables.getFirst(functions.getFunctionNameAndArguments("{{hello(\"1\")}}"), null));
    }

    @Test
    public void functionAndArgNumberText() {
        assertArrayEquals(new Object[]{"hello", "1", "String"}, Iterables.getFirst(functions.getFunctionNameAndArguments("{{hello(1,\"String\")}}"), null));
    }

    @Test
    public void repeatAndArgText() {
        assertArrayEquals(null, functions.getRepeatFunctionNameAndArguments("'{{repeat(\"1\")}}',"));
    }

    @Test
    public void repeatAndArgNumber() {
        assertArrayEquals(new Object[]{"repeat", "1"}, functions.getRepeatFunctionNameAndArguments("'{{repeat(1)}}',"));
    }

    @Test
    public void repeatAndArgMultipleNumbers() {
        assertArrayEquals(null, functions.getRepeatFunctionNameAndArguments("'{{repeat(1,2)}}',"));
    }

    @Test
    public void repeatAndArgNumberAndSpaces() {
        assertArrayEquals(new Object[]{"repeat", "1"}, functions.getRepeatFunctionNameAndArguments("'{{repeat(1)}}',   \n\n\n"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidFunctionWithArgs() {
        test("invalid(1,5)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidFunction() {
        test("invalid()");
    }

    @Test
    public void integer() {
        String result = test("integer(1,5)");
        Integer integer = Integer.parseInt(result);
        assertTrue(integer >= 1 && integer <= 5);
    }

    @Test
    public void doubleFunction() {
        String result = test("double(1.2,20.3)");
        Double doub = Double.parseDouble(result);
        assertTrue(doub >= 1.2 && doub <= 20.3);
    }

    @Test
    public void floatFunction() {
        String result = test("float(1352.2335345,7563.36456464)");
        Float flo = Float.parseFloat(result);
        assertTrue(flo >= 1352.2335345f && flo <= 7563.36456464);
    }

    @Test
    public void floatWithFormat() {
        String result = test("float(1.1241241,10.36456464,\"%.2f\")");
        Float.parseFloat(result);
        assertDecimalPlaces(result, 2);
    }

    private void assertDecimalPlaces(String text, int decimalPlaces) {
        int integerPlaces = text.indexOf('.');
        assertEquals(decimalPlaces, text.length() - integerPlaces - 1);
    }

    @Test
    public void longFunctionWithSuffix() {
        String result = test("long(1L,90210L)");
        Long longNumber = Long.parseLong(result);
        assertTrue(longNumber >= 1L && longNumber <= 90210L);
    }

    @Test
    public void longFunction() {
        String result = test("long(1,90210)");
        Long longNumber = Long.parseLong(result);
        assertTrue(longNumber >= 1L && longNumber <= 90210L);
    }

    @Test
    public void randomUUID() {
        String result = test("uuid()");
        assertNotNull(result);
    }

    @Test
    public void bool() {
        String result = test("bool()");
        Boolean bool = Boolean.parseBoolean(result);
        assertNotNull(bool);
    }

    @Test
    public void concatStrings() {
        String result = test("concat(\"red\",\"blue\",\"green\")");
        assertEquals("redbluegreen", result);
    }

    @Test
    public void concatNumbersAndStrings() {
        String result = test("concat(1,\"blue\",2)");
        assertEquals("1blue2", result);
    }

    @Test
    public void randomString() {
        String result = test("random(red,blue,green)");
        assertTrue("red".equals(result) || "blue".equals(result) || "green".equals(result));
    }

    @Test
    public void randomStringWithQuotes() {
        String result = test("random(\"red\",\"blue\",\"green\")");
        assertTrue("red".equals(result) || "blue".equals(result) || "green".equals(result));
    }

    @Test
    public void randomDouble() {
        String result = test("random(1.0,2.89,4.09)");
        assertTrue("1.0".equals(result) || "2.89".equals(result) || "4.09".equals(result));
    }

    @Test
    public void index() {
        String result = test("index()");
        assertEquals(0, Integer.parseInt(result));
        result = test("index()");
        assertEquals(1, Integer.parseInt(result));
    }

    @Test
    public void loremWords() {
        String result = test("lorem(3,\"words\")");
        assertTrue(result.startsWith("lorem ipsum "));
    }

    @Test
    public void loremParagraphs() {
        String result = test("lorem(2,paragraphs)");
        assertTrue(result.startsWith("\tLorem ipsum "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void loremInvalid() {
        String result = test("lorem(3,invalid)");
    }

    @Test
    public void phone() {
        String result = test("phone()");
        Pattern phoneRegex = Pattern.compile("^\\d{3}-\\d{3}-\\d{4}$");
        assertTrue(phoneRegex.matcher(result).matches());
    }

    @Test
    public void gender() {
        String result = test("gender()");
        assertTrue("male".equals(result) || "female".equals(result));
    }

    @Test
    public void substring() {
        String result = test("substring(gender(),3)");
        assertTrue("e".equals(result) || "ale".equals(result));
    }

    @Test
    public void substring2Args() {
        String result = test("substring(phone(),0,3)");
        Integer integer = Integer.parseInt(result);
        assertTrue(integer < 1000);
        assertEquals(3, result.length());
    }

    @Test
    public void toUpperCaseAnotherFunction() {
        String result = test("toUpperCase(gender())");
        assertTrue("male".toUpperCase().equals(result) || "female".toUpperCase().equals(result));
    }

    @Test
    public void toLowerCaseAnotherFunction() {
        String result = test("toLowerCase(random(\"RED\",\"Blue\",\"GrEEN\"))");
        assertTrue("red".equals(result) || "blue".equals(result) || "green".equals(result));
    }

    @Test
    public void date() throws ParseException {
        String result = test("date()");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        dateFormat.parse(result);
    }

    @Test
    public void dateWithFormat() throws ParseException {
        String dateFormatText = "MMM yyyy HH:mm:ss z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        String result = test("date(" + dateFormatText + ")");
        dateFormat.parse(result);
    }

    @Test
    public void dateWithinRange() throws ParseException {
        String dateFormatText = "EEE, d MMM yyyy HH:mm:ss z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        String result = test("date(06-16-1956 12:00:00,06-16-1975 12:00:00)");
        dateFormat.parse(result);
    }

    @Test
    public void dateWithinRangeWithFormat() throws ParseException {
        String dateFormatText = "MMM yyyy HH:mm:ss z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        String result = test("date(06-16-1956 12:00:00,06-16-1975 12:00:00," + dateFormatText + ")");
        dateFormat.parse(result);
    }

    @Test
    public void country() {
        notNullNoArgTest("country");
    }

    @Test
    public void city() {
        notNullNoArgTest("city");
    }

    @Test
    public void state() {
        notNullNoArgTest("state");
    }

    @Test
    public void street() {
        notNullNoArgTest("street");
    }

    @Test
    public void company() {
        notNullNoArgTest("company");
    }

    @Test
    public void firstName() {
        notNullNoArgTest("firstName");
    }

    @Test
    public void lastName() {
        notNullNoArgTest("lastName");
    }

    @Test
    public void email() {
        notNullNoArgTest("email");
    }

    private void notNullNoArgTest(String functionName) {
        String result = functions.execute(functionName, null);
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    private String test(String string) {
        TokenResolver tokenResolver = new FunctionTokenResolver();
        return tokenResolver.resolveToken(indexHolder, "{{" + string + "}}");
    }

}
