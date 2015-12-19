package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name="repeat", overridable = false)
public class Repeat {

    @FunctionInvocation
    public String getRepeatCount(String count) {
        return Integer.valueOf(count).toString();
    }
}
