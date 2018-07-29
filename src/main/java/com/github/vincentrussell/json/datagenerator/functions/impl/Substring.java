package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = "substring")
public class Substring {

    @FunctionInvocation
    public String substring(String value, String beginIndex) {
        return value.substring(Integer.valueOf(beginIndex));
    }

    @FunctionInvocation
    public String substring(String value, String beginIndex, String endIndex) {
        return value.substring(Integer.valueOf(beginIndex), Integer.valueOf(endIndex));
    }
}
