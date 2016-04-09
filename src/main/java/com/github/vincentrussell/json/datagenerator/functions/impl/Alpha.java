package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Random;

@Function(name = "alpha")
public class Alpha {

    private static final Random RANDOM = new Random();

    @FunctionInvocation
    public String getAlpha(String min, String max) {
        int randomInt = getRandomInteger(Integer.parseInt(min), Integer.parseInt(max));
        return getRandomAlphabetic(randomInt);
    }

    @FunctionInvocation
    public String getAlpha() {
        int randomInt = getRandomInteger(10, 20);
        return getRandomAlphabetic(randomInt);
    }

    @FunctionInvocation
    public String getAlpha(String length) {
        return getRandomAlphabetic(Integer.parseInt(length));
    }

    private int getRandomInteger(Integer min, Integer max) {
        return RANDOM.nextInt(max - min) + min;
    }

    public String getRandomAlphabetic(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

}
