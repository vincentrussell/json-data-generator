package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;

import static org.apache.commons.lang.Validate.notNull;

/**
 * get a value from the cache
 */
@Function(name = "get")
public class Get {

    private final FunctionRegistry functionRegistry;

    public Get(final FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    /**
     * get a value from the cache
     * @param key the key used to put the value in the cache
     * @return the value that was found in the cache with the key
     */
    @FunctionInvocation
    public String get(final String key) {
        String value = functionRegistry.getGetAndPutCache().get(key);
        notNull(value, "could not find a value for key: " + key);
        return value;
    }

}
