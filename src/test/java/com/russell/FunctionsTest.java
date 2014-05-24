package com.russell;

import com.russell.json.impl.FunctionsImpl;
import com.russell.json.impl.IndexHolder;
import org.junit.Test;

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
    public void loremWorlds() {
        String result = functions.execute("lorem",new Object[]{3,"words"});
        assertEquals("Lorem ipsum dolor", result);
    }

    @Test
    public void loremParagraphs() {
        String result = functions.execute("lorem",new Object[]{1,"paragraphs"});
        assertEquals("Lorem ipsum dolor sit amet,"+
                " consetetur sadipscing elitr, sed diam nonumy eirmod"+
                " tempor invidunt ut labore et dolore magna aliquyam erat,"+
                " sed diam voluptua. At vero eos et accusam et justo duo dolores"+
                " et ea rebum. Stet clita kasd gubergren, no sea "+
                "takimata sanctus est Lorem ipsum dolor sit amet.", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void loremInvalid() {
        String result = functions.execute("lorem",new Object[]{3,"invalid"});
    }

}
