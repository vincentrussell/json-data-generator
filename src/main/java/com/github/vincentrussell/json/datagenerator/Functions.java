package com.github.vincentrussell.json.datagenerator;


import java.util.regex.Pattern;

public interface Functions {
    public String execute(String functionName, Object... arguments) throws IllegalArgumentException;
    public boolean isFunction(CharSequence input);
    public boolean isRepeatFunction(CharSequence input);
    public Object[] getFunctionNameAndArguments(CharSequence input);
    public Object[] getRepeatFunctionNameAndArguments(CharSequence input);
    public Object[] getFunctionNameAndArguments(CharSequence input, Pattern pattern);
}
