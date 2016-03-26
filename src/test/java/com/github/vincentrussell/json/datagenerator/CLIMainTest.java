package com.github.vincentrussell.json.datagenerator;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.google.gson.JsonObject;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.security.Permission;
import java.util.List;

import static com.github.vincentrussell.json.datagenerator.CLIMain.ENTER_JSON_TEXT;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

public class CLIMainTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Rule
    public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    File sourceFile;
    File destinationFile;


    @Before
    public void before() throws IOException {
        sourceFile = temporaryFolder.newFile();
        destinationFile = temporaryFolder.newFile();
    }

    @Test
    public void missingArgumentsThrowsExceptionAndPrintsHelp() throws ClassNotFoundException, JsonDataGeneratorException, ParseException, IOException {
        exception.expect(ParseException.class);
        exception.expectMessage("Missing required option: s or i");
        CLIMain.main(new String[0]);
    }

    @Test
    public void interactiveMode() throws IOException, JsonDataGeneratorException, ParseException, ClassNotFoundException, InterruptedException {
        String name = "A green door";
        exit.expectSystemExitWithStatus(0);
        systemInMock.provideLines("{\n" +
                "    \"id\": \"{{uuid()}}\",\n" +
                "    \"name\": \"" + name + "\",\n" +
                "    \"age\": {{integer(1,50)}},\n" +
                "    \"price\": 12.50,\n" +
                "    \"tags\": [\"home\", \"green\"]\n" +
                "}");
        try {
            CLIMain.main(new String[]{"-i"});
        } finally {
            Thread.sleep(1000);
            assertThat(systemOutRule.getLog(),startsWith(ENTER_JSON_TEXT));
            String result = systemOutRule.getLog().replaceAll(ENTER_JSON_TEXT,"");
            JsonObject obj = (JsonObject)new com.google.gson.JsonParser().parse(result);
            assertEquals(name,obj.get("name").getAsString());

        }
    }

    @Test(expected = FileNotFoundException.class)
    public void sourceFileNotFound() throws IOException, JsonDataGeneratorException, ParseException, ClassNotFoundException {
        sourceFile.delete();
        CLIMain.main(new String[]{"-s", sourceFile.getAbsolutePath(), "-d", destinationFile.getAbsolutePath()});
    }

    @Test(expected = IOException.class)
    public void destinationExists() throws IOException, JsonDataGeneratorException, ParseException, ClassNotFoundException {
        CLIMain.main(new String[]{"-s", sourceFile.getAbsolutePath(), "-d", destinationFile.getAbsolutePath()});
    }

    @Test
    public void successfulRun() throws IOException, JsonDataGeneratorException, ParseException, ClassNotFoundException {
        destinationFile.delete();
        try (FileOutputStream fileOutputStream = new FileOutputStream(sourceFile)) {
            IOUtils.write("{\n" +
                    "    \"id\": \"{{uuid()}}\",\n" +
                    "    \"name\": \"A green door\",\n" +
                    "    \"age\": {{integer(1,50)}},\n" +
                    "    \"price\": 12.50,\n" +
                    "    \"tags\": [\"home\", \"green\"]\n" +
                    "}", fileOutputStream);
            CLIMain.main(new String[]{"-s", sourceFile.getAbsolutePath(), "-d", destinationFile.getAbsolutePath()});
            assertTrue(destinationFile.exists());
            try (FileInputStream fileInputStream = new FileInputStream(destinationFile)) {
                List list = IOUtils.readLines(fileInputStream);
                assertEquals(7, list.size());
            }

        }
    }

    @Test
    public void successfulRunSystemOut() throws IOException, JsonDataGeneratorException, ParseException, ClassNotFoundException {
        destinationFile.delete();
        try (FileOutputStream fileOutputStream = new FileOutputStream(sourceFile)) {
            String name = "A green door";
            IOUtils.write("{\n" +
                    "    \"id\": \"{{uuid()}}\",\n" +
                    "    \"name\": \"" + name + "\",\n" +
                    "    \"age\": {{integer(1,50)}},\n" +
                    "    \"price\": 12.50,\n" +
                    "    \"tags\": [\"home\", \"green\"]\n" +
                    "}", fileOutputStream);
            CLIMain.main(new String[]{"-s", sourceFile.getAbsolutePath()});
            JsonObject obj = (JsonObject)new com.google.gson.JsonParser().parse(systemOutRule.getLog());
            assertEquals(name,obj.get("name").getAsString());
        }
    }

    @Test
    public void registerAdditionalFunction() throws IOException, JsonDataGeneratorException, ParseException, ClassNotFoundException {
        destinationFile.delete();
        try (FileOutputStream fileOutputStream = new FileOutputStream(sourceFile)) {
            IOUtils.write("{\n" +
                    "    \"test-function\": \"{{test-function()}},\"\n" +
                    "    \"test-function2\": \"{{test-function2()}}\"\n" +
                    "}", fileOutputStream);
            CLIMain.main(new String[]{"-s", sourceFile.getAbsolutePath(), "-d", destinationFile.getAbsolutePath(),
                    "-f", TestFunctionClazzWithNoArgsMethod.class.getName(), TestFunctionClazzWithNoArgsMethod2.class.getName()});
            assertTrue(destinationFile.exists());
            String result = FileUtils.readFileToString(destinationFile);
            assertEquals("{\n" +
                    "    \"test-function\": \"ran successfully,\"\n" +
                    "    \"test-function2\": \"ran successfully 2\"\n" +
                    "}",result);
            }
    }

    @Function(name = "test-function")
    public static class TestFunctionClazzWithNoArgsMethod {

        @FunctionInvocation
        public String invocation() {
            return "ran successfully";
        }


    }

    @Function(name = "test-function2")
    public static class TestFunctionClazzWithNoArgsMethod2 {

        @FunctionInvocation
        public String invocation() {
            return "ran successfully 2";
        }


    }

    private static class NoExitSecurityManager extends SecurityManager
    {
        @Override
        public void checkPermission(Permission perm)
        {
            // allow anything.
        }
        @Override
        public void checkPermission(Permission perm, Object context)
        {
            // allow anything.
        }
        @Override
        public void checkExit(int status) {
            super.checkExit(status);
        }
    }

}
