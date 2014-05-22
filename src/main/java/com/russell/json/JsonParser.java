package com.russell.json;


import java.io.OutputStream;
import java.io.Reader;

public interface JsonParser {
    public void generateTestDataJson(String text, OutputStream outputStream);
    public void generateTestDataJson(Reader reader, OutputStream outputStream);
    public boolean isFunction(CharSequence input);
    public boolean isRepeatFunction(CharSequence input);
}
