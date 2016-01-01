package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.TokenResolver;
import com.github.vincentrussell.json.datagenerator.functions.ObjectRegistry;
import com.github.vincentrussell.json.datagenerator.parser.FunctionParser;
import com.github.vincentrussell.json.datagenerator.parser.ParseException;
import com.google.common.collect.Iterables;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionTokenResolver implements TokenResolver {

    public static final Pattern FUNCTION_PATTERN = Pattern.compile("\\{\\{(.+)\\}\\}");
    public static final Pattern FUNCTION_PATTERN_2 = Pattern.compile("([\\w]+)\\((.*)\\)");
    public static final Pattern REPEAT_FUNCTION_PATTERN = Pattern.compile("\'\\{\\{(repeat)\\((\\d+)\\)\\}\\}\'\\s*,");
    public static final String NESTED_RESULT = "NESTED_RESULT";

    ObjectRegistry objectRegistry = ObjectRegistry.getInstance();

    @Override
    public String resolveToken(IndexHolder indexHolder, CharSequence s) {
        objectRegistry.register(IndexHolder.class, indexHolder);

        try {
            FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream(s.toString().getBytes()));
            return functionParser.Parse();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static boolean isRepeatFunction(CharSequence input) {
        Matcher matcher = REPEAT_FUNCTION_PATTERN.matcher(input);
        return matcher.find();
    }

    public static List<List<Object>> getFunctionNameAndArguments(CharSequence input) {
        return getFunctionNameAndArguments(input, FUNCTION_PATTERN_2);
    }

    public static List<Object> getRepeatFunctionNameAndArguments(CharSequence input) {
        List<List<Object>> objects = getFunctionNameAndArguments(input, REPEAT_FUNCTION_PATTERN);
        if (objects == null) {
            return null;
        }
        return Iterables.getFirst(objects, null);
    }

    public static List<List<Object>> getFunctionNameAndArguments(CharSequence input, Pattern pattern) {
        List<List<Object>> objectList = new ArrayList<>();
        getFunctionNameAndArguments(objectList, input, pattern);
        return objectList;
    }


    private static CharSequence getFunctionNameAndArguments(List<List<Object>> outerList, CharSequence input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        List<Object> objectList = new ArrayList<Object>();
        if (matcher.find()) {
            String functionNameSection = matcher.group(1);
            objectList.add(functionNameSection);
            String argSection = matcher.group(2);
            Matcher matcher2 = pattern.matcher(argSection);
            String[] args = argSection.length() == 0 ? new String[0] : argSection.split(",(?![^(]*\\))");
            if (matcher2.matches()) {
                String zero = matcher2.group(0);
                CharSequence nestedFunctionName = getFunctionNameAndArguments(outerList, zero, pattern);
                if (!argSection.equals(nestedFunctionName)) {
                    addArgsToObjectList(nestedFunctionName, pattern, objectList, args, outerList);
                } else {
                    objectList.add(NESTED_RESULT);
                }
                outerList.add(objectList);
                return input;
            } else if (args != null && args.length > 0) {
                addArgsToObjectList(null, pattern, objectList, args, outerList);
                outerList.add(objectList);
                return input;
            } else {
                outerList.add(objectList);
                return input;
            }
        }
        return null;
    }

    private static void addArgsToObjectList(CharSequence nestedFunctionName, Pattern pattern, List<Object> objectList, String[] args, List<List<Object>> list) {
        for (String arg : args) {
            if (arg == null || arg.length() == 0) {
                continue;
            }
            Matcher matcher = pattern.matcher(arg);
            if (matcher.matches()) {
                getFunctionNameAndArguments(list, arg, pattern);
                objectList.add(NESTED_RESULT);
            } else {
                String value = arg.replaceAll("^\"|\"$", "");
                if (value.equals(nestedFunctionName)) {
                    value = NESTED_RESULT;
                }
                objectList.add(value);
            }
        }
    }


}
