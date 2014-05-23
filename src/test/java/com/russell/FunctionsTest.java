package com.russell;

import com.russell.json.impl.FunctionsImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionsTest {

    FunctionsImpl functions = new FunctionsImpl();

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

}
