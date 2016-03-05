package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = "random")
public class Random {

    @FunctionInvocation
    public String random(String... strings) {
        int randomNum = (int) (Math.random() * strings.length);
        return strings[randomNum];
    }

}
