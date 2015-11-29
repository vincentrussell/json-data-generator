package com.github.vincentrussell.json.datagenerator;


import java.util.List;
import java.util.regex.Pattern;

public interface Functions {
    String execute(String functionName, Object... arguments) throws IllegalArgumentException;
    boolean isFunction(CharSequence input);
    boolean isRepeatFunction(CharSequence input);
    List<List<Object>> getFunctionNameAndArguments(CharSequence input);
    List<Object> getRepeatFunctionNameAndArguments(CharSequence input);
    List<List<Object>> getFunctionNameAndArguments(CharSequence input, Pattern pattern);
}
