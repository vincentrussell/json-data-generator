package com.github.vincentrussell.json.datagenerator;


import java.io.File;
import java.io.OutputStream;
import java.net.URL;

public interface JsonDataGenerator {

    /**
     * Generate json test data
     * @param text source json text
     * @param outputStream stream to write the test data to
     * @throws JsonDataGeneratorException
     */
    public void generateTestDataJson(String text, OutputStream outputStream) throws JsonDataGeneratorException;

    /**
     * Generate json test data
     * @param classPathResource url of source json text on classpath
     * @param outputStream stream to write the test data to
     * @throws JsonDataGeneratorException
     */
    public void generateTestDataJson(URL classPathResource, OutputStream outputStream) throws JsonDataGeneratorException;

    /**
     * Generate json test data
     * @param file file of source json text
     * @param outputStream stream to write the test data to
     * @throws JsonDataGeneratorException
     */
    public void generateTestDataJson(File file, OutputStream outputStream) throws JsonDataGeneratorException;
}
