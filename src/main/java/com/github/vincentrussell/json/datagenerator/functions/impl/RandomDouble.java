package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Random;

@Function(name = "double")
public class RandomDouble {

    private static final Random RANDOM = new Random();

    @FunctionInvocation
    public String getRandomDouble(String min, String max) {
        return getRandomDouble(Double.parseDouble(min), Double.parseDouble(max));
    }

    private String getRandomDouble(Double min, Double max) {
        double randomNumber = min + (max - min) * RANDOM.nextDouble();
        return Double.toString(randomNumber);
    }

}
