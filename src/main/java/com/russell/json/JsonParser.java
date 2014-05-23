package com.russell.json;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.URL;

public interface JsonParser {
    public void generateTestDataJson(String text, OutputStream outputStream) throws JsonParserException;
    public void generateTestDataJson(URL classPathResource, OutputStream outputStream) throws JsonParserException;
    public void generateTestDataJson(File file, OutputStream outputStream) throws JsonParserException;
}
