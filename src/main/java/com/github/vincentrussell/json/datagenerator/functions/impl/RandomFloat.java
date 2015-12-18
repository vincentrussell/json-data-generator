package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Random;

@Function(name = "float")
public class RandomFloat {

    private static final Random RANDOM = new Random();

    private String getRandomFloat(Float min, Float max, String format) {
        float randomNumber = min + (max - min) * RANDOM.nextFloat();

        if (format != null) {
            return String.format(format, randomNumber);
        }
        return Float.toString(randomNumber);
    }

    private String getRandomFloat(Float min, Float max) {
        return getRandomFloat(min, max, null);
    }

    @FunctionInvocation
    public String getRandomFloat(String min, String max) {
        return getRandomFloat(min, max, null);
    }

    @FunctionInvocation
    public String getRandomFloat(String min, String max, String format) {
        return getRandomFloat(Float.parseFloat(min), Float.parseFloat(max), format);
    }

}
