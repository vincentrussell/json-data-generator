package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Locale;

/**
 * upper case a string
 */
@Function(name = "toUpperCase")
public class ToUpper {

    /**
     * upper case a string
     * @param string input string
     * @return the result
     */
    @FunctionInvocation
    public String toUpperCase(final String string) {
        return string.toUpperCase(Locale.getDefault());
    }
}
