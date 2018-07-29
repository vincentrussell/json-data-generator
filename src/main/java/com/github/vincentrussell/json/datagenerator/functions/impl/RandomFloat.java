package com.github.vincentrussell.json.datagenerator.functions.impl;

import java.util.Formatter;
import java.util.Random;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * get random float between min and max
 */
@Function(name = {"float", "floating"})
public class RandomFloat {

    private static final Random RANDOM = new Random();

    private String getRandomFloat(final Float min, final Float max, final String format) {
        float randomNumber = min + (max - min) * RANDOM.nextFloat();

        if (format != null) {
            return String.format(format, randomNumber);
        }
        return Float.toString(randomNumber);
    }

    /**
     * get random float
     * @param min min number
     * @param max max number
     * @return the result
     */
    @FunctionInvocation
    public String getRandomFloat(final String min, final String max) {
        return getRandomFloat(min, max, null);
    }

    /**
     * get random number with format (eg. "%.2f")
     * @param min min number
     * @param max max number
     * @param format format in {@link Formatter} format
     * @return the result
     */
    @FunctionInvocation
    public String getRandomFloat(final String min, final String max, final String format) {
        return getRandomFloat(Float.parseFloat(min), Float.parseFloat(max), format);
    }

}
