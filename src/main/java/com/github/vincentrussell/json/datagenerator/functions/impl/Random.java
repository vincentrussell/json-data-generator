package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * random item from list
 */
@Function(name = "random")
public class Random {

    /**
     * random item from list:
     * @param strings options to choose from
     * @return the result
     */
    @FunctionInvocation
    public String random(final String... strings) {
        int randomNum = (int) (Math.random() * strings.length);
        return strings[randomNum];
    }

}
