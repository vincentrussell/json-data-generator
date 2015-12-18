package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = "gender")
public class Gender {

    @FunctionInvocation
    public String gender() {
        return (Math.random() < 0.5) ? "male" : "female";
    }
}
