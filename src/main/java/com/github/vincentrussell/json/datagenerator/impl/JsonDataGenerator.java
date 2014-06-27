package com.github.vincentrussell.json.datagenerator.impl;


import java.io.File;
import java.io.OutputStream;
import java.net.URL;

public interface JsonDataGenerator {
    public void generateTestDataJson(String text, OutputStream outputStream) throws JsonDataGeneratorException;
    public void generateTestDataJson(URL classPathResource, OutputStream outputStream) throws JsonDataGeneratorException;
    public void generateTestDataJson(File file, OutputStream outputStream) throws JsonDataGeneratorException;
}
