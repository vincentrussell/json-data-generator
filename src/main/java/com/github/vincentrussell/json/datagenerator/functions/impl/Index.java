package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.functions.ObjectRegistry;
import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;

@Function(name = "index")
public class Index {

    ObjectRegistry objectRegistry = ObjectRegistry.getInstance();

    @FunctionInvocation
    public String getIndex() {
        return "" + objectRegistry.getInstance(IndexHolder.class).getNextIndex();
    }
}
