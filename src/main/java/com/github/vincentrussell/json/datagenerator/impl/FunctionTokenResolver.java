package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.Functions;
import com.github.vincentrussell.json.datagenerator.TokenResolver;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.List;

public class FunctionTokenResolver implements TokenResolver {

    public FunctionTokenResolver() {}

    @Override
    public String resolveToken(IndexHolder indexHolder, CharSequence s) {
        Functions functions = new FunctionsImpl(indexHolder);
        List<Object[]> functionAndArgList = functions.getFunctionNameAndArguments(s);
        if (functionAndArgList==null) {
            return null;
        }
        String result = null;
        for (Object[] functionAndArgs : functionAndArgList) {
            String functionName = (String)functionAndArgs[0];
            Object[] arrays = Arrays.copyOfRange(functionAndArgs,1,functionAndArgs.length);
            if (arrays.length == 0 && result != null) {
                result = functions.execute(functionName,result);
            } else {
                for (int i=0; i < arrays.length;i++) {
                    if (FunctionsImpl.NESTED_RESULT.equals(arrays[i]) && result!=null) {
                        arrays[i] = result;
                    }
                }
                result = functions.execute(functionName,arrays);
            }

        }


        return result;
    }


}
