package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.Functions;
import com.github.vincentrussell.json.datagenerator.TokenResolver;

import java.util.Arrays;

public class FunctionTokenResolver implements TokenResolver {

    public FunctionTokenResolver() {}

    @Override
    public String resolveToken(IndexHolder indexHolder, CharSequence s) {
        Functions functions = new FunctionsImpl(indexHolder);
        Object[] functionAndArgs = functions.getFunctionNameAndArguments(s);
        if (functionAndArgs==null) {
            return null;
        }
        String functionName = (String)functionAndArgs[0];
        return functions.execute(functionName,Arrays.copyOfRange(functionAndArgs,1,functionAndArgs.length));
    }


}
