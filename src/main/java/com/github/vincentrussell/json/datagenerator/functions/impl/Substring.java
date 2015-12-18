package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = "substring")
public class Substring {

    @FunctionInvocation
    public String substring(String value, String beginIndex) {
        return value.substring(Integer.valueOf(beginIndex));
    }

    private String substring(String value, int beginIndex) {
        return value.substring(beginIndex);
    }

    @FunctionInvocation
    public String substring(String value, String beginIndex, String endIndex) {
        return value.substring(Integer.valueOf(beginIndex), Integer.valueOf(endIndex));
    }

    private String substring(String value, int beginIndex, int endIndex) {
        return value.substring(beginIndex, endIndex);
    }

}
