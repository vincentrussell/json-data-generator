package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Random;

/**
 * get random double in range
 */
@Function(name = "double")
public class RandomDouble {

    private static final Random RANDOM = new Random();

    /**
     * get random double in range
     * @param min min number
     * @param max max number
     * @return the result
     */
    @FunctionInvocation
    public String getRandomDouble(final String min, final String max) {
        return getRandomDouble(Double.parseDouble(min), Double.parseDouble(max));
    }

    private String getRandomDouble(final Double min, final Double max) {
        double randomNumber = min + (max - min) * RANDOM.nextDouble();
        return Double.toString(randomNumber);
    }

}
