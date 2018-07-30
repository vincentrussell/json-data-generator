package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * function to return a random gender
 */
@Function(name = "gender")
public class Gender {

    /**
     * random gender with 50/50 possibility of being male
     * @return the result
     */
    @SuppressWarnings("checkstyle:magicnumber")
    @FunctionInvocation
    public String gender() {
        return getMaleOrFemale((float) 0.5);
    }

    private String getMaleOrFemale(final float probability) {
        return (Math.random() < probability) ? "male" : "female";
    }

    /**
     * random gender with probability of being male
     * @param probability float probability of being male
     * @return the result
     */
    @FunctionInvocation
    public String gender(final String probability) {
        return Boolean.valueOf(Math.random() < Float.parseFloat(probability)).toString();
    }
}
