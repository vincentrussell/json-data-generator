package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import org.apache.commons.lang.RandomStringUtils;

/**
 * random string with alpha-numeric characters (defaults to between 10 and 20 characters)
 */
@Function(name = "alphaNumeric")
public class AlphaNumeric {

    /**
     * Function call with min and max arguments
     * @param min minium length
     * @param max maximum length
     * @return the result
     */
    @FunctionInvocation
    public String getAlphaNumeric(final String min, final String max) {
        int randomInt = FunctionUtils.getRandomInteger(Integer.parseInt(min),
            Integer.parseInt(max));
        return getRandomAlphabetic(randomInt);
    }

    /**
     * Default function call
     * @return the result
     */
    @FunctionInvocation
    @SuppressWarnings("checkstyle:magicnumber")
    public String getAlphaNumeric() {
        int randomInt = FunctionUtils.getRandomInteger(10, 20);
        return getRandomAlphabetic(randomInt);
    }

    /**
     * Function call with length
     * @param length length of string
     * @return the result
     */
    @FunctionInvocation
    public String getAlphaNumeric(final String length) {
        return getRandomAlphabetic(Integer.parseInt(length));
    }

    private String getRandomAlphabetic(final int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

}
