package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Function(name = "long")
public class RandomLong {

    public static final Pattern LONG_PATTERN = Pattern.compile("(\\d+)L");
    public static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();

    @FunctionInvocation
    public String getRandomLong(String min, String max) {
        return getRandomLong(parseLong(min), parseLong(max));
    }

    private String getRandomLong(Long min, Long max) {
        long randomNumber = RANDOM_DATA_GENERATOR.nextLong(min, max);
        return Long.toString(randomNumber);
    }


    private Long parseLong(String string) {
        Matcher matcher = LONG_PATTERN.matcher(string);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1));
        }
        return Long.parseLong(string);
    }
}
