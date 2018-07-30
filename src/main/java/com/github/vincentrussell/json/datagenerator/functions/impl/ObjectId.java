package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * random objectId (12 byte hex string):
 */
@Function(name = "objectId")
public class ObjectId {

    private final Hex hex = new Hex();

    /**
     * random objectId (12 byte hex string):
     * @return the result
     */
    @FunctionInvocation
    public String getObjectId() {
        return hex.hex("12");
    }

}
