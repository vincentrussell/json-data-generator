package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = {"uuid","guid"})
public class UUID {

    @FunctionInvocation
    public String getRandomUUID() {
        return java.util.UUID.randomUUID().toString();
    }
}
