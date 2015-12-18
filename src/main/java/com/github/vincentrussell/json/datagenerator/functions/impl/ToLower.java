package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Locale;

@Function(name = "toLowerCase")
public class ToLower {

    @FunctionInvocation
    public String toLowerCase(String string) {
        return string.toLowerCase(Locale.getDefault());
    }
}
