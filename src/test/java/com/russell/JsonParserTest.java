package com.russell;

import com.russell.json.impl.JsonParserImpl;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;


public class JsonParserTest
{

    private JsonParserImpl parser = new JsonParserImpl();
    ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() {
        try {
            outputStream.close();
        } catch (IOException e) {
            //noop
        }
    }


    @Test
    public void copyJson() throws IOException {
        InputStream resultsStream = this.getClass().getClassLoader().getResourceAsStream("copyJson.json.results");
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("copyJson.json"), outputStream);
        String results  = new String(outputStream.toByteArray());
        assertEquals(IOUtils.toString(resultsStream).trim(),results.trim());

    }

    @Test
    public void copyDoubleNestedJson() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("copyDoubleNestedJson.json");
        InputStream resultsStream = this.getClass().getClassLoader().getResourceAsStream("copyDoubleNestedJson.results");
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("copyDoubleNestedJson.json"), outputStream);
        String results  = new String(outputStream.toByteArray());
        assertEquals(IOUtils.toString(resultsStream).trim(), results.trim());

    }


    @Test
    public void functionSimpleJson() throws IOException {
        InputStream resultsStream = this.getClass().getClassLoader().getResourceAsStream("simple.json.results");
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("simple.json"), outputStream);
        String results  = new String(outputStream.toByteArray());
        assertEquals(IOUtils.toString(resultsStream).trim(),results.trim());

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
