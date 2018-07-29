package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * concat strings together
 */
@Function(name = "concat")
public class Concat {

    /**
     * function call
     * @param objects the strings to concat
     * @return the result
     */
    @FunctionInvocation
    public String concat(final String... objects) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : objects) {
            stringBuilder.append(object.toString());
        }
        return stringBuilder.toString();
    }

}
