package com.russell.json.impl;


import com.russell.json.Functions;
import com.russell.json.TokenResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionTokenResolver implements TokenResolver {

    Functions functions = new FunctionsImpl();

    public FunctionTokenResolver() {

    }

    @Override
    public String resolveToken(CharSequence s) {
        Object[] functionAndArgs = getFunctionNameAndArguments(s);
        String functionName = (String)functionAndArgs[0];
        return functions.execute(functionName,Arrays.copyOfRange(functionAndArgs,1,functionAndArgs.length));
    }

    public Object[] getFunctionNameAndArguments(CharSequence input) {
        return getFunctionNameAndArguments(input,FunctionsImpl.FUNCTION_PATTERN);
    }

    public Object[] getRepeatFunctionNameAndArguments(CharSequence input) {
        return getFunctionNameAndArguments(input, FunctionsImpl.REPEAT_FUNCTION_PATTERN);
    }

    public Object[] getFunctionNameAndArguments(CharSequence input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        List<Object> objectList = new ArrayList<Object>();
        if (matcher.find()) {
            objectList.add(matcher.group(1));
            for (String arg : matcher.group(2).split(",")) {
                if (arg==null || arg.length()==0) {
                    continue;
                }
                try {
                    objectList.add(Integer.valueOf(arg));
                } catch (NumberFormatException e) {
                    objectList.add(arg.replaceAll("^\"|\"$", ""));
                }

            }
            return objectList.toArray();
        }
        return null;
    }
}
