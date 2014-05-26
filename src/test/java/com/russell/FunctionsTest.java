package com.russell;

import com.russell.json.impl.FunctionsImpl;
import com.russell.json.impl.IndexHolder;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class FunctionsTest {

    private IndexHolder indexHolder = new IndexHolder();
    private FunctionsImpl functions = new FunctionsImpl(indexHolder);

    @Test
    public void isFunctionSimple() {
        assertTrue(functions.isFunction("{{hello()}}"));
    }

    @Test
    public void isFunction1Arg() {
        assertTrue(functions.isFunction("{{hello(1)}}"));
    }

    @Test
    public void functionAndArgNumber() {
        assertArrayEquals(new Object[]{"hello",1},functions.getFunctionNameAndArguments("{{hello(1)}}"));
    }

    @Test
    public void functionAndArgText() {
        assertArrayEquals(new Object[]{"hello","1"},functions.getFunctionNameAndArguments("{{hello(\"1\")}}"));
    }

    @Test
    public void functionAndArgNumberText() {
        assertArrayEquals(new Object[]{"hello",1,"String"},functions.getFunctionNameAndArguments("{{hello(1,\"String\")}}"));
    }

    @Test
    public void repeatAndArgText() {
        assertArrayEquals(null,functions.getRepeatFunctionNameAndArguments("'{{repeat(\"1\")}}',"));
    }

    @Test
    public void repeatAndArgNumber() {
        assertArrayEquals(new Object[]{"repeat",1},functions.getRepeatFunctionNameAndArguments("'{{repeat(1)}}',"));
    }

    @Test
    public void repeatAndArgMultipleNumbers() {
        assertArrayEquals(null,functions.getRepeatFunctionNameAndArguments("'{{repeat(1,2)}}',"));
    }

    @Test
    public void repeatAndArgNumberAndSpaces() {
        assertArrayEquals(new Object[]{"repeat",1},functions.getRepeatFunctionNameAndArguments("'{{repeat(1)}}',   \n\n\n"));
    }

    @Test
    public void integer() {
        String result = functions.execute("integer",new Object[]{1,5});
        Integer integer = Integer.parseInt(result);
        assertTrue(true);
    }

    @Test
    public void randomUUID() {
        String result = functions.execute("uuid",null);
        assertNotNull(result);
    }

    @Test
    public void bool() {
        String result = functions.execute("bool",null);
        Boolean bool = Boolean.parseBoolean(result);
        assertNotNull(bool);
    }

    @Test
    public void randomString() {
        String result = functions.execute("random",new Object[]{"red","blue","green"});
        assertTrue("red".equals(result) || "blue".equals(result) || "green".equals(result));
    }

    @Test
    public void randomDouble() {
        String result = functions.execute("random",new Object[]{1.0,2.89,4.09});
        assertTrue("1.0".equals(result) || "2.89".equals(result) || "4.09".equals(result));
    }

    @Test
    public void index() {
        String result = functions.execute("index",null);
        assertEquals(0, Integer.parseInt(result));
        result = functions.execute("index",null);
        assertEquals(1, Integer.parseInt(result));
    }

    @Test
    public void loremWords() {
        String result = functions.execute("lorem",new Object[]{3,"words"});
        assertTrue(result.startsWith("lorem ipsum "));
    }

    @Test
    public void loremParagraphs() {
        String result = functions.execute("lorem",new Object[]{2,"paragraphs"});
        assertTrue(result.startsWith("\tLorem ipsum "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void loremInvalid() {
        String result = functions.execute("lorem",new Object[]{3,"invalid"});
    }

    @Test
    public void phone() {
        String result = functions.execute("phone",null);
        Pattern phoneRegex = Pattern.compile("^\\d{3}-\\d{3}-\\d{4}$");
        assertTrue(phoneRegex.matcher(result).matches());
    }

    @Test
    public void gender() {
        String result = functions.execute("gender",null);
        assertTrue("male".equals(result) || "female".equals(result));
    }

    @Test
    public void date() throws ParseException {
        String result = functions.execute("date",null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        dateFormat.parse(result);
    }

    @Test
    public void dateWithFormat() throws ParseException {
        String dateFormatText = "MMM yyyy HH:mm:ss z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        String result = functions.execute("date",new Object[]{dateFormatText});
        dateFormat.parse(result);
    }

    @Test
    public void dateWithinRange() throws ParseException {
        String dateFormatText = "EEE, d MMM yyyy HH:mm:ss z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        String result = functions.execute("date",new Object[]{"06-16-1956 12:00:00","06-16-1975 12:00:00"});
        dateFormat.parse(result);
    }

    @Test
    public void dateWithinRangeWithFormat() throws ParseException {
        String dateFormatText = "MMM yyyy HH:mm:ss z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        String result = functions.execute("date",new Object[]{"06-16-1956 12:00:00","06-16-1975 12:00:00",dateFormatText});
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
        String result = functions.execute(functionName,null);
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

}
