package com.github.vincentrussell.json.datagenerator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.github.vincentrussell.json.datagenerator.impl.JsonParserException;
import com.github.vincentrussell.json.datagenerator.impl.impl.JsonParserImpl;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;


public class JsonParserTest {

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


    private void classpathJsonTests(String expected, String source) throws IOException, JsonParserException {
        InputStream resultsStream = this.getClass().getClassLoader().getResourceAsStream(expected);
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource(source), outputStream);
        String results  = new String(outputStream.toByteArray());

        assertEquals(remoteWhiteSpaceFromJson(IOUtils.toString(resultsStream)),
                          remoteWhiteSpaceFromJson(results));
    }

    private String remoteWhiteSpaceFromJson(String json) {
        JsonElement jsonObject = new com.google.gson.JsonParser().parse(json);
        return jsonObject.toString();
    }


    @Test
    public void copyJson() throws IOException, JsonParserException {
        classpathJsonTests("copyJson.json.results","copyJson.json");

    }

    @Test
    public void copyDoubleNestedJson() throws IOException, JsonParserException {
        classpathJsonTests("copyDoubleNestedJson.json.results","copyDoubleNestedJson.json");

    }

    @Test
    public void invallidFunction() throws IOException, JsonParserException {
        classpathJsonTests("invalidFunction.json.results","invalidFunction.json");
    }

    @Test
    public void repeatNonFunctionJsonArray() throws IOException, JsonParserException {
        classpathJsonTests("repeatNonFunctionJsonArray.json.results","repeatNonFunctionJsonArray.json");
    }

    @Test
    public void indexFunctionTest() throws IOException, JsonParserException {
        classpathJsonTests("indexFunctionSimple.json.results", "indexFunctionSimple.json");
    }

    @Test
    public void repeatFunctionJsonArrayNoQuotes() throws IOException, JsonParserException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("repeatFunctionJsonArrayNoQuotes.json"), outputStream);
        String results  = new String(outputStream.toByteArray());
        JsonObject obj = (JsonObject)new com.google.gson.JsonParser().parse(results);
        JsonArray array = obj.getAsJsonArray("numbers");
        assertEquals(3,array.size());
    }

    @Test
    public void repeatFunctionJsonArrayQuotes() throws IOException, JsonParserException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("repeatFunctionJsonArrayQuotes.json"), outputStream);
        String results  = new String(outputStream.toByteArray());
        JsonObject obj = (JsonObject)new com.google.gson.JsonParser().parse(results);
        JsonArray array = obj.getAsJsonArray("colors");
        assertEquals(4, array.size());
    }


    @Test
    public void functionSimpleJson() throws IOException, JsonParserException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("simple.json"), outputStream);
        String results  = new String(outputStream.toByteArray());
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        JsonObject obj = (JsonObject)parser.parse(results);
        assertEquals("A green door", obj.get("name").getAsString());
        assertEquals(12.50,obj.get("price").getAsDouble(),0);
    }

}
