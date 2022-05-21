package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * functions with time
 */
@Function(name = "time")
public class Time {

    public static final String DEFAULT_INPUT_FORMAT = "h[:mm][ ]a";
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter
            .ofPattern(DEFAULT_INPUT_FORMAT);

    /**
     * generate a time representing "now"
     * @return the result
     */
    @FunctionInvocation
    public String time() {
        return time(DEFAULT_INPUT_FORMAT);
    }

    /**
     * generate a time representing now with a particular time format
     * @param outputTimeFormat the output time format
     * @return the formatted time
     */
    @FunctionInvocation
    public String time(final String outputTimeFormat) {
        return format(LocalTime.now(), outputTimeFormat);
    }
    private String format(final LocalTime localTime, final String defaultInputFormat) {
        return localTime.format(DateTimeFormatter.ofPattern(defaultInputFormat));
    }


    /**
     * random time in between two ranges
     * @param startTime in the following format "h[:mm][ ]a"
     * @param endTime in the following format "h[:mm][ ]a"
     * @param outputFormat the output time format
     * @return the formatted time
     */
    @FunctionInvocation
    public String time(final String startTime, final String endTime,
                       final String outputFormat) {
        LocalTime time1 = LocalTime.parse(startTime, DEFAULT_FORMATTER);
        LocalTime time2 = LocalTime.parse(endTime, DEFAULT_FORMATTER);
        int secondOfDayTime1 = time1.toSecondOfDay();
        int secondOfDayTime2 = time2.toSecondOfDay();
        Random random = new Random();
        int randomSecondOfDay = secondOfDayTime1 + random
                .nextInt(secondOfDayTime2 - secondOfDayTime1);
        return LocalTime.ofSecondOfDay(randomSecondOfDay).format(DateTimeFormatter
                .ofPattern(outputFormat));
    }

}
