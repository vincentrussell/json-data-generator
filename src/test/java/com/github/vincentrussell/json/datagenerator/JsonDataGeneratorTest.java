package com.github.vincentrussell.json.datagenerator;

import com.github.approval.Approvals;
import com.github.approval.reporters.Reporters;
import com.github.vincentrussell.json.datagenerator.functions.impl.Index;
import com.github.vincentrussell.json.datagenerator.impl.JsonDataGeneratorImpl;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.*;


public class JsonDataGeneratorTest{

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
        ((Map<?, ?>)ReflectionTestUtils.getField(Index.class,"STRING_INDEX_HOLDER_MAP")).clear();
        Approvals.setReporter(Reporters.console());
    }

    @After
    public void tearDown() {
        try {
            outputStream.close();
        } catch (IOException e) {
            //noop
        }
    }


    private void classpathJsonTests(String source) throws IOException, JsonDataGeneratorException {
        Approvals.verify(getClasspathFileAsString(source), getApprovalPath(source));
    }

    private String getClasspathFileAsString(String source) throws IOException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(source)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new JsonDataGeneratorImpl().generateTestDataJson(inputStream, byteArrayOutputStream);
            return byteArrayOutputStream.toString("UTF-8");
        } catch (Throwable t) {
            throw new RuntimeException(t);
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

    private Path getApprovalPath(String testName) {
        final String basePath = Paths.get("src", "test", "resources", "approvals", JsonDataGeneratorTest.class.getSimpleName()).toString();
        return Paths.get(basePath, testName);
    }

    @Test
    public void copyRepeatsDoesNotAddNullCharacters() throws IOException, JsonDataGeneratorException {
        File file = temporaryFolder.newFile();

        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("large_repeats.json");
        FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            new JsonDataGeneratorImpl().generateTestDataJson(inputStream, fileOutputStream);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        assertTrue(file.exists());
       assertFalse(Hex.encodeHexString(FileUtils.readFileToByteArray(file)).contains("0000"));
    }


    @Test
    public void utf8TestForeignCharacters() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("foreignCharacters.json");
    }

    @Test
    public void copyJson() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("copyJson.json");
    }

    @Test
    public void copyDoubleNestedJson() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("copyDoubleNestedJson.json");
    }

    @Test
    public void invalidFunction() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("invalidFunction.json");
    }

    @Test
    public void zeroRepeat() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("zeroRepeat.json");
    }

    @Test
    public void zeroRepeatRange() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("zeroRepeatRange.json");
    }

    @Test
    public void repeatNonFunctionJsonArray() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("repeatNonFunctionJsonArray.json");
    }

    @Test
    public void indexFunctionTest() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("indexFunctionSimple.json");
    }

    @Test
    public void indexFunctionWithNamesTest() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("indexFunctionNested.json");
    }

    @Test
    public void putGetTest() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("putGetTest.json");
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
    public void concatWithBracesEscaped() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson("{\n" +
                "    \"concattest\": \"{{concat(\"\\{\", \"test\", \"\\}\")}}\",\n" +
                "}", outputStream);
        String results = new String(outputStream.toByteArray());
        assertEquals("{\n" +
                "    \"concattest\": \"{test}\",\n" +
                "}",results);
    }


    @Test
    public void singleQuoteFunctionTest() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson("{\"day1\": \"{{put('date', date('dd-MM-yyyy HH:mm:ss'))}}\",\"day2\": \"{{addDays(get('date'), 12)}}\"}", outputStream);
        String results = new String(outputStream.toByteArray());
        JsonObject obj = (JsonObject) new com.google.gson.JsonParser().parse(results);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        assertIsDate(dateFormat, obj.getAsJsonPrimitive("day1").getAsString());
        assertIsDate(dateFormat, obj.getAsJsonPrimitive("day2").getAsString());
    }

    private void assertIsDate(SimpleDateFormat dateFormat, String date) {
        try {
            Date dateObj = dateFormat.parse(date);
            assertNotNull(dateObj);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void doubleQuoteFunctionTest() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson("{\"day1\": \"{{put(\"date\", date(\"dd-MM-yyyy HH:mm:ss\"))}}\",\"day2\":\"{{addDays(get(\"date\"), 12)}}\"}\n", outputStream);
        String results = new String(outputStream.toByteArray());
        JsonObject obj = (JsonObject) new com.google.gson.JsonParser().parse(results);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        assertIsDate(dateFormat, obj.getAsJsonPrimitive("day1").getAsString());
        assertIsDate(dateFormat, obj.getAsJsonPrimitive("day2").getAsString());
    }

    @Test
    public void concatWithEscapesWithoutBraces() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson("{\n" +
                "    \"concattest\": \"{{concat(\"\\\\\", \"test\", \"\\}\")}}\",\n" +
                "}", outputStream);
        String results = new String(outputStream.toByteArray());
        assertEquals("{\n" +
                "    \"concattest\": \"\\\\test}\",\n" +
                "}",results);
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
    public void resetIndex() throws IOException, JsonDataGeneratorException {
        classpathJsonTests("resetIndex.json");
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
        assertTrue(Pattern.compile("\\{\n" +
                "    \"id\": \"dfasf235345345\",\n" +
                "    \"name\": \"A green door\",\n" +
                "    \"age\": 23,\n" +
                "    \"price\": 12.50,\n" +
                "    \"numbers\": \\['\\{\\{repeat\\(3\\)\\},\n" +
                "             \\d+\\]\n" +
                "}", Pattern.MULTILINE).matcher(result).matches());
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

    @Test
    public void foreignCharactersInTokenResolver() throws IOException, JsonDataGeneratorException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("foreignCharactersWithinTokenResolver.json"), outputStream);
        String results = new String(outputStream.toByteArray());
        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        JsonObject obj = (JsonObject) parser.parse(results);
        String randomResult = obj.get("random").getAsString();
        ArrayList<String> randomOptions =
            Lists.newArrayList("中文替换", "Как тебя зовут?", "هناك أولاد في الحديقة");
        assertTrue(randomOptions.contains(randomResult));
    }

    @Test
    public void testInvalidScenario()
        throws JsonDataGeneratorException, UnsupportedEncodingException {
      JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      parser.generateTestDataJson("{{", outputStream);
      String output = outputStream.toString("UTF-8");
      assertTrue("{{}}".equals(output));
    }
    
    @Test
    public void testXmlTemplate() throws IOException, JsonDataGeneratorException, SAXException, ParserConfigurationException, XpathException {
        parser.generateTestDataJson(this.getClass().getClassLoader().getResource("xmlfunctionWithRepeat.xml"), outputStream);
        
        ByteArrayInputStream inputstream = new ByteArrayInputStream(outputStream.toByteArray());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setIgnoringComments(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(inputstream);
        XpathEngine simpleXpathEngine = XMLUnit.newXpathEngine();
        String value = simpleXpathEngine.evaluate("//root/tags", doc);
		assertEquals(value.split(",").length, 7);
		assertTrue(simpleXpathEngine.evaluate("//root/element[1]/name", doc).length() > 1);
		assertTrue(simpleXpathEngine.evaluate("//root/element[2]/name", doc).length() > 1);
		assertTrue(simpleXpathEngine.evaluate("//root/friends/friend[1]/name", doc).length() > 1);
		assertTrue(simpleXpathEngine.evaluate("//root/friends/friend[2]/name", doc).length() > 1);
		assertTrue(simpleXpathEngine.evaluate("//root/friends/friend[3]/name", doc).length() > 1);
    }
}
