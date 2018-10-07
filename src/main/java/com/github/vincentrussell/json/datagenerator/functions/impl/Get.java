package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import static org.apache.commons.lang.Validate.notNull;

/**
 * store a value in the cache
 */
@Function(name = "get")
public class Get {

    /**
     * get a value from the cache
     * @param key the key used to put the value in the cache
     * @return the value that was found in the cache with the key
     */
    @FunctionInvocation
    public String get(final String key) {
        String value = Put.CACHE.get(key);
        notNull(value, "could not find a value for key: " + key);
        return value;
    }

}
