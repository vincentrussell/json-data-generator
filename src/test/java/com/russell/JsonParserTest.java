package com.russell;

import com.russell.json.impl.JsonParserImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;


public class JsonParserTest
{

    private JsonParserImpl parser = new JsonParserImpl();
    ByteArrayOutputStream os;

    @Before
    public void setUp() {
        os = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() {
        try {
            os.close();
        } catch (IOException e) {
            //noop
        }
    }


    @Test
    public void copyJson() {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("copyJson.json");
        parser.generateTestDataJson(stream,os);
        String results  = new String(os.toByteArray());
        System.out.println(results);

    }

    @Test
    public void copyDoupleNestedJson() {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("copyDoubleNestedJson.json");
        parser.generateTestDataJson(stream,os);
        String results  = new String(os.toByteArray());
        System.out.println(results);

    }

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

    @Test
    public void repeatAndArgText() {
        assertArrayEquals(null,parser.getRepeatFunctionNameAndArguments("'{{repeat(\"1\")}}',"));
    }

    @Test
    public void repeatAndArgNumber() {
        assertArrayEquals(new Object[]{"repeat",1},parser.getRepeatFunctionNameAndArguments("'{{repeat(1)}}',"));
    }

    @Test
    public void repeatAndArgMultipleNumbers() {
        assertArrayEquals(null,parser.getRepeatFunctionNameAndArguments("'{{repeat(1,2)}}',"));
    }

    @Test
    public void repeatAndArgNumberAndSpaces() {
        assertArrayEquals(new Object[]{"repeat",1},parser.getRepeatFunctionNameAndArguments("'{{repeat(1)}}',   \n\n\n"));
    }
}
