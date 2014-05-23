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

    public FunctionTokenResolver() {}

    @Override
    public String resolveToken(CharSequence s) {
        Object[] functionAndArgs = functions.getFunctionNameAndArguments(s);
        String functionName = (String)functionAndArgs[0];
        return functions.execute(functionName,Arrays.copyOfRange(functionAndArgs,1,functionAndArgs.length));
    }


}
