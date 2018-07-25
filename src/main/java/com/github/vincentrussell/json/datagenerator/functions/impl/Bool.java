package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = "bool")
public class Bool {

    @FunctionInvocation
    public String getRandomBool() {
        return Boolean.valueOf(Math.random() < 0.5).toString();
    }
    
    @FunctionInvocation
    public String getRandomBool(String chance) {
    	return Boolean.valueOf(Math.random() < Float.parseFloat(chance)).toString();
    }
}
