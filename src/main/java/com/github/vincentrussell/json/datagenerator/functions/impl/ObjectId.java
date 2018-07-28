package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = "objectId")
public class ObjectId {

    private final Hex hex = new Hex();

    @FunctionInvocation
    public String getObjectId() {
        return hex.hex("12");
    }

}
