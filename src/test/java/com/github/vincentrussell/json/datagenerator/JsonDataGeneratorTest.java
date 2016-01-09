package com.github.vincentrussell.json.datagenerator;

import com.github.vincentrussell.json.datagenerator.impl.JsonDataGeneratorImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


public class JsonDataGeneratorTest {

    private JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
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


    private void classpathJsonTests(String expected, String source) throws IOException, JsonDataGeneratorException {
        try (InputStream resultsStream = this.getClass().getClassLoader().getResourceAsStream(expected)) {
            parser.generateTestDataJson(this.getClass().getClassLoader().getResource(source), outputStream);
            String results = new String(outputStream.toByteArray());

            assertEquals(remoteWhiteSpaceFromJson(IOUtils.toString(resultsStream)),
                    remoteWhiteSpaceFromJson(results));
        }
    }

    private String getClasspathFileAsString(String source) throws IOException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(source)) {
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer, "UTF-8");
            return writer.toString();
        }
    }

    private String remoteWhiteSpaceFromJson(String json) {
        try {
            JsonElement jsonObject = new com.google.gson.JsonParser().parse(json);
            return jsonObject.toString();
        } catch (JsonSyntaxException e) {
            return json.trim();
        }
    }


    @Test
    public void copyJson() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("copyJson.json.results", "copyJson.json");

    }

    @Test
    public void copyDoubleNestedJson() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("copyDoubleNestedJson.json.results", "copyDoubleNestedJson.json");

    }

    @Test
    public void invalidFunction() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("invalidFunction.json.results", "invalidFunction.json");
    }

    @Test
    public void repeatNonFunctionJsonArray() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("repeatNonFunctionJsonArray.json.results", "repeatNonFunctionJsonArray.json");
    }

    @Test
    public void indexFunctionTest() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("indexFunctionSimple.json.results", "indexFunctionSimple.json");
    }

    @Test
    public void repeatFunctionInvalid() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("repeatFunctionInvalid.json"), outputStream);
        String result = outputStream.toString();
        Pattern pattern = Pattern.compile("\\{\n" +
                "    \"id\": \"dfasf235345345\",\n" +
                "    \"name\": \"A green door\",\n" +
                "    \"age\": 23,\n" +
                "    \"price\": 12\\.50,\n" +
                "    \"numbers\": \\['\\{\\{repeat\\(3\\)},\n" +
                "             \\d+]\n" +
                "}", Pattern.MULTILINE);
        assertTrue(pattern.matcher(result).find());
    }

    @Test
    public void repeatFunctionJsonArrayNoQuotes() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("repeatFunctionJsonArrayNoQuotes.json"), outputStream);
        String results = new String(outputStream.toByteArray());
        JsonObject obj = (JsonObject) new com.google.gson.JsonParser().parse(results);
        JsonArray array = obj.getAsJsonArray("numbers");
        assertEquals(3, array.size());
    }

    @Test
    public void repeatFunctionJsonArrayQuotes() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("repeatFunctionJsonArrayQuotes.json"), outputStream);
        String results = new String(outputStream.toByteArray());
        JsonObject obj = (JsonObject) new com.google.gson.JsonParser().parse(results);
        JsonArray array = obj.getAsJsonArray("colors");
        assertEquals(4, array.size());
    }


    @Test
    public void functionSimpleJson() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("simple.json"), outputStream);
        String results = new String(outputStream.toByteArray());
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        JsonObject obj = (JsonObject) parser.parse(results);
        assertEquals("A green door", obj.get("name").getAsString());
        assertEquals(12.50, obj.get("price").getAsDouble(), 0);
    }

}
