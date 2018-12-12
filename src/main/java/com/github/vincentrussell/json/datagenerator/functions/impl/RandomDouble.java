package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Formatter;
import java.util.Random;

/**
 * get random double in range
 */
@Function(name = "double")
public class RandomDouble {

    private static final Random RANDOM = new Random();

    private String getRandomDouble(final Double min, final Double max, final String format) {
        double randomNumber = min + (max - min) * RANDOM.nextDouble();

        if (format != null) {
            return String.format(format, randomNumber);
        }
        return Double.toString(randomNumber);
    }

    /**
     * get random double in range
     * @param min min number
     * @param max max number
     * @return the result
     */
    @FunctionInvocation
    public String getRandomDouble(final String min, final String max) {
        return getRandomDouble(Double.parseDouble(min), Double.parseDouble(max), null);
    }

    /**
     * get random number with format (eg. "%.2f")
     * @param min min number
     * @param max max number
     * @param format format in {@link Formatter} format
     * @return the result
     */
    @FunctionInvocation
    public String getRandomDouble(final String min, final String max, final String format) {
        return getRandomDouble(Double.parseDouble(min), Double.parseDouble(max), format);
    }

}
