package com.github.vincentrussell.json.datagenerator;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CLIMainTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    File sourceFile;
    File destinationFile;


    @Before
    public void before() throws IOException {
        sourceFile = temporaryFolder.newFile();
        destinationFile = temporaryFolder.newFile();
    }


    @Test(expected = ParseException.class)
    public void missingArguments() throws IOException, JsonDataGeneratorException, ParseException {
        CLIMain.main(new String[]{""});
    }

    @Test(expected = FileNotFoundException.class)
    public void sourceFileNotFound() throws IOException, JsonDataGeneratorException, ParseException {
        sourceFile.delete();
        CLIMain.main(new String[]{"-s", sourceFile.getAbsolutePath(),"-d", destinationFile.getAbsolutePath()});
    }

    @Test(expected = IOException.class)
    public void destinationExists() throws IOException, JsonDataGeneratorException, ParseException {
        CLIMain.main(new String[]{"-s", sourceFile.getAbsolutePath(),"-d", destinationFile.getAbsolutePath()});
    }

    @Test
    public void successfulRun() throws IOException, JsonDataGeneratorException, ParseException {
        destinationFile.delete();
        try (FileOutputStream fileOutputStream = new FileOutputStream(sourceFile)) {
            IOUtils.write("{\n" +
                    "    \"id\": \"{{uuid()}}\",\n" +
                    "    \"name\": \"A green door\",\n" +
                    "    \"age\": {{integer(1,50)}},\n" +
                    "    \"price\": 12.50,\n" +
                    "    \"tags\": [\"home\", \"green\"]\n" +
                    "}", fileOutputStream);
            CLIMain.main(new String[]{"-s", sourceFile.getAbsolutePath(),"-d", destinationFile.getAbsolutePath()});
            assertTrue(destinationFile.exists());
            try (FileInputStream fileInputStream = new FileInputStream(destinationFile)) {
                List list = IOUtils.readLines(fileInputStream);
                assertEquals(7,list.size());
            }

        }
    }

}
