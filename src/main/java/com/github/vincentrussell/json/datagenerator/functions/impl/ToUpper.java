package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Locale;

@Function(name = "toUpperCase")
public class ToUpper {

    @FunctionInvocation
    public String toUpperCase(String string) {
        return string.toUpperCase(Locale.getDefault());
    }
}
