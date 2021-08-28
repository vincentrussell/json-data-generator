package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;

import static org.apache.commons.lang.Validate.notNull;

/**
 * store a value in the cache
 */
@Function(name = "put")
public class Put {

    private final FunctionRegistry functionRegistry;

    public Put(final FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    /**
     * put a value in the cache
     * @param key the key used to identify the value
     * @param value the value
     * @return the value passed in
     */
    @FunctionInvocation
    public String put(final String key, final String value) {
        notNull(key, "key to put is null");
        notNull(value, "value to put is null");
        functionRegistry.getGetAndPutCache().put(key, value);
        return value;
    }

}
