package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import static org.apache.commons.lang.Validate.notNull;

/**
 * get a value from a system property
 */
@Function(name = "systemProperty")
public class SystemProperty {


    /**
     * get a value from the system properties
     * @param key key from the system property
     * @return the value from the system property
     */
    @FunctionInvocation
    public String get(final String key) {
        String value = System.getProperty(key);
        notNull(value, "could not find a value for key: " + key);
        return value;
    }

}
