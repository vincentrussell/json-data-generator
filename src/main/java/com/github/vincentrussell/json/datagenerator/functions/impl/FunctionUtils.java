package com.github.vincentrussell.json.datagenerator.functions.impl;

import java.util.Random;

/**
 * Function utilities
 */
public final class FunctionUtils {

    /**
     * default private constructor
     */
    private FunctionUtils() {

    }

    private static final Random RANDOM = new Random();

    /**
     * return a random element in array
     * @param array the array with elements to choose from
     * @return random element
     */
    public static String getRandomElementFromArray(final String[] array) {
        int randomNumber = getRandomInteger(0, array.length);
        return array[randomNumber];
    }

    /**
     * get random integer between min and max
     * @param min minimum integer
     * @param max maximum integer
     * @return random integer
     */
    public static int getRandomInteger(final Integer min, final Integer max) {
        return RANDOM.nextInt(max - min) + min;
    }

}
