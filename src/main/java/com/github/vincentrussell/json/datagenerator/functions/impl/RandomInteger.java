package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Random;

@Function(name = "integer")
public class RandomInteger {

    private static final Random RANDOM = new Random();

    @FunctionInvocation
    public String getRandomInteger(String min, String max) {
        return getRandomInteger(Integer.parseInt(min), Integer.parseInt(max));
    }

    private String getRandomInteger(Integer min, Integer max) {
        int randomNumber = RANDOM.nextInt(max - min) + min;
        return Integer.toString(randomNumber);
    }
}
