package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Random;

/**
 * function used to handle repeats within the json.  Just returns the number
 * of repeats that should be done.
 */
@Function(name = "repeat")
public final class Repeat {

    /**
     * gets the string of a random integer between
     * lower range (inclusive) and upper range (inclusive)
     * @param lowerRange lower range
     * @param upperRange upper range
     * @return the string of a random integer between
     * lower range (inclusive) and upper range (inclusive)
     */
    @FunctionInvocation
    public String repeat(final String lowerRange, final String upperRange) {
            Integer integer = Integer.parseInt(lowerRange);
            Integer integer2 = Integer.parseInt(upperRange);
            if (integer >= integer2 && !integer.equals(integer2)) {
                throw new IllegalArgumentException(
                        "the second number, " + upperRange
                                + ", must be greater than the first number, " + upperRange);
            } else if (integer.equals(integer2)) {
                return integer.toString();
            } else {
                return Integer.valueOf((
                        new Random().nextInt((integer2 - integer) + 1) + integer)).toString();
            }
    }

    /**
     * gets the string form of the passed in integer
     * @param integer integer as string
     * @return the string form of the passed in integer
     */
    @FunctionInvocation
    public String repeat(final String integer) {
       return Integer.valueOf(integer).toString();
    }

}
