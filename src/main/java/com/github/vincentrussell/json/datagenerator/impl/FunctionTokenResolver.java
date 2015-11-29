package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.Functions;
import com.github.vincentrussell.json.datagenerator.TokenResolver;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class FunctionTokenResolver implements TokenResolver {

    public FunctionTokenResolver() {}

    @Override
    public String resolveToken(IndexHolder indexHolder, CharSequence s) {
        Functions functions = new FunctionsImpl(indexHolder);
        List<List<Object>> functionAndArgList = functions.getFunctionNameAndArguments(s);
        if (functionAndArgList==null) {
            return null;
        }
        String result = null;
        for (List<Object> functionAndArgs : functionAndArgList) {
            String functionName = (String)Iterables.get(functionAndArgs,0);
            List<Object> sublist = functionAndArgs.subList(1,functionAndArgs.size());
            if (sublist.size() == 0 && result != null) {
                result = functions.execute(functionName,result);
            } else {
                for (int i=0; i < sublist.size();i++) {
                    if (FunctionsImpl.NESTED_RESULT.equals(Iterables.get(sublist,i)) && result!=null) {
                        sublist.set(i,result);
                    }
                }
                result = functions.execute(functionName,sublist.toArray());
            }

        }


        return result;
    }


}
