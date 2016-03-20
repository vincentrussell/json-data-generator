package com.github.vincentrussell.json.datagenerator;

import com.github.vincentrussell.json.datagenerator.impl.JsonDataGeneratorImpl;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;


public class JsonDataGeneratorTest {

    private JsonDataGeneratorImpl parser;
    ByteArrayOutputStream outputStream;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        parser = new JsonDataGeneratorImpl();
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
    public void sourceFileNotFound() throws JsonDataGeneratorException {
        expectedException.expect(JsonDataGeneratorException.class);
        expectedException.expectCause(isA(FileNotFoundException.class));
        parser.generateTestDataJson(new File("notfound"),new File("notfound"));
    }

    @Test
    public void destinationFileExists() throws JsonDataGeneratorException, IOException {
        File inputFile = temporaryFolder.newFile();
        File outputFile = temporaryFolder.newFile();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("simple.json");
        FileOutputStream fileOutputStream = new FileOutputStream(inputFile)) {
            IOUtils.copy(inputStream,fileOutputStream);
        }

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("outputFile can not exist");
        parser.generateTestDataJson(inputFile,outputFile);
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
    public void repeatFunctionRangeJsonArrayNoQuotes() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("repeatFunctionRangeJsonArrayNoQuotes.json"), outputStream);
        String results = new String(outputStream.toByteArray());
        JsonObject obj = (JsonObject) new com.google.gson.JsonParser().parse(results);
        int numberSize = obj.getAsJsonArray("numbers").size();
        Assert.assertTrue(numberSize >= 3 && numberSize <= 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void repeatFunctionInvalidRange() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson("{\n" +
                "    \"id\": \"dfasf235345345\",\n" +
                "    \"name\": \"A green door\",\n" +
                "    \"age\": 23,\n" +
                "    \"price\": 12.50,\n" +
                "    \"numbers\": ['{{repeat(10,3)}}',\n" +
                "             {{integer(1,100)}}]\n" +
                "}", outputStream);
    }


    @Test
    public void repeatFunctionRangeEqual() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson("{\n" +
                "    \"id\": \"dfasf235345345\",\n" +
                "    \"name\": \"A green door\",\n" +
                "    \"age\": 23,\n" +
                "    \"price\": 12.50,\n" +
                "    \"numbers\": ['{{repeat(10,10)}}',\n" +
                "             {{integer(1,100)}}]\n" +
                "}", outputStream);
        String results = new String(outputStream.toByteArray());
        JsonObject obj = (JsonObject) new com.google.gson.JsonParser().parse(results);
        int numberSize = obj.getAsJsonArray("numbers").size();
        assertEquals(10,numberSize);
    }

    @Test
    public void floatingRepeatsWithQuotes() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson("[\n" +
                "  '{{repeat(1, 1)}}',\n" +
                "  {\n" +
                "    _id: '{{objectId()}}',\n" +
                "    index: '{{index()}}',\n" +
                "    guid: '{{guid()}}',\n" +
                "    isActive: '{{bool()}}',\n" +
                "    balance: '{{floating(1000, 4000)}}',\n" +
                "    picture: 'http://placehold.it/32x32',\n" +
                "    age: '{{integer(20, 40)}}',\n" +
                "    eyeColor: '{{random(\"blue\", \"brown\", \"green\")}}',\n" +
                "    name: '{{firstName()}} {{surname()}}',\n" +
                "    gender: '{{gender()}}',\n" +
                "    company: '{{company().toUpperCase()}}',\n" +
                "    email: '{{email()}}',\n" +
                "    phone: '+1 {{phone()}}',\n" +
                "    address: '{{integer(100, 999)}} {{street()}}, {{city()}}, {{state()}}, {{integer(100, 10000)}}',\n" +
                "    about: '{{lorem(1, \"paragraphs\")}}',\n" +
                "    registered: '{{date()}}',\n" +
                "    latitude: '{{floating(-90.000001, 90)}}',\n" +
                "    longitude: '{{floating(-180.000001, 180)}}',\n" +
                "    tags: [\n" +
                "      '{{repeat(7)}}',\n" +
                "      '{{lorem(1, \"words\")}}'\n" +
                "    ],\n" +
                "    friends: [\n" +
                "      '{{repeat(3)}}',\n" +
                "      {\n" +
                "        id: '{{index()}}',\n" +
                "        name: '{{firstName()}} {{surname()}}'\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]", outputStream);
        String results = new String(outputStream.toByteArray());
        JsonObject obj = (JsonObject) ((JsonArray) new com.google.gson.JsonParser().parse(results)).get(0);
        int numberSize = obj.getAsJsonArray("tags").size();
        assertEquals(7,numberSize);

        for (JsonElement tagElement : obj.getAsJsonArray("tags")) {
            JsonPrimitive primitive = tagElement.getAsJsonPrimitive();
            assertTrue(primitive.isString());
        }
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
