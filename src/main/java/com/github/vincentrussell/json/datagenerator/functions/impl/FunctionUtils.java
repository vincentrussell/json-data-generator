package com.github.vincentrussell.json.datagenerator.functions.impl;

import java.util.Random;

public class FunctionUtils {

    private static final Random RANDOM = new Random();

    public static String getRandomElementFromArray(String[] array) {
        int randomNumber = getRandomInteger(0, array.length);
        return array[randomNumber];
    }

    public static int getRandomInteger(Integer min, Integer max) {
        return RANDOM.nextInt(max - min) + min;
    }

}
