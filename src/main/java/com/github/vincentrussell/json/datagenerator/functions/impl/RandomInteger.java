package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Random;

/**
 * get Random integer
 */
@Function(name = "integer")
public class RandomInteger {

    private static final Random RANDOM = new Random();

    /**
     * get random integer within range
     * @param min minimum number
     * @param max maximum number
     * @return the result
     */
    @FunctionInvocation
    public String getRandomInteger(final String min, final String max) {
        return getRandomInteger(Integer.parseInt(min), Integer.parseInt(max));
    }

    private String getRandomInteger(final Integer min, final Integer max) {
        return Integer.toString(FunctionUtils.getRandomInteger(min, max));
    }
}
