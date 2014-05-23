package com.russell;

import com.russell.json.impl.FunctionsImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionsTest {

    FunctionsImpl functions = new FunctionsImpl();

    @Test
    public void randomInt() {
        String result = functions.execute("randomInt",new Object[]{1,5});
        Integer integer = Integer.parseInt(result);
        assertTrue(true);
    }

    @Test
    public void randomUUID() {
        String result = functions.execute("uuid",null);
        assertNotNull(result);
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

}
