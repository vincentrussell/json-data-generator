package com.russell.json;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

public interface JsonParser {
    public void generateTestDataJson(String text, OutputStream outputStream);
    public void generateTestDataJson(InputStream inputstream, OutputStream outputStream);
    public boolean isFunction(CharSequence input);
    public boolean isRepeatFunction(CharSequence input);
}
