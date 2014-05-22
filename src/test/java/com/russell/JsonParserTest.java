package com.russell;

import com.russell.json.JsonParser;
import org.junit.Test;
import static org.junit.Assert.*;


public class JsonParserTest
{

    private JsonParser parser = new JsonParser();


//    @Test
//    public void input()
//    {
//        parser.generateTestDataJson("{\"hello\":34}");
//    }
//
//    @Test
//    public void nestedObject()
//    {
//        parser.generateTestDataJson("{\"hello\":{\"goodbye\":\"yeahright\"}}");
//    }

    @Test
    public void isFunctionSimple() {
        assertTrue(parser.isFunction("{{hello()}}"));
    }

    @Test
    public void isFunction1Arg() {
        assertTrue(parser.isFunction("{{hello(1)}}"));
    }

    @Test
    public void functionAndArgNumber() {
        assertArrayEquals(new Object[]{"hello",1},parser.getFunctionNameAndArguments("{{hello(1)}}"));
    }

    @Test
    public void functionAndArgText() {
        assertArrayEquals(new Object[]{"hello","1"},parser.getFunctionNameAndArguments("{{hello(\"1\")}}"));
    }

    @Test
    public void functionAndArgNumberText() {
        assertArrayEquals(new Object[]{"hello",1,"String"},parser.getFunctionNameAndArguments("{{hello(1,\"String\")}}"));
    }
}
