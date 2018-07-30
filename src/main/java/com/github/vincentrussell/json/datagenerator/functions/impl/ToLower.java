package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Locale;

/**
 * lower case a string
 */
@Function(name = "toLowerCase")
public class ToLower {

    /**
     * lower case a string
     * @param string input string
     * @return the result
     */
    @FunctionInvocation
    public String toLowerCase(final String string) {
        return string.toLowerCase(Locale.getDefault());
    }
}
