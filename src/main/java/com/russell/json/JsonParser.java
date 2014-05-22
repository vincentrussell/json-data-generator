package com.russell.json;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.URL;

public interface JsonParser {
    public void generateTestDataJson(String text, OutputStream outputStream);
    public void generateTestDataJson(URL classPathResource, OutputStream outputStream);
    public void generateTestDataJson(File file, OutputStream outputStream) throws FileNotFoundException;
    public boolean isFunction(CharSequence input);
    public boolean isRepeatFunction(CharSequence input);
}
