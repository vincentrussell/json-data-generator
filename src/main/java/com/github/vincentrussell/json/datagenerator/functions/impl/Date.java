package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * functions with dates
 */
@Function(name = "date")
public class Date {

    public static final String DEFAULT_DATE_STRING = "EEE, d MMM yyyy HH:mm:ss z";
    public static final String DEFAULT_INPUT_FORMAT = "dd-MM-yyyy HH:mm:ss";

    /**
     * generate a date with "now"
     * @return the result
     */
    @FunctionInvocation
    public String date() {
        return getSimpleDateFormat().format(new java.util.Date());
    }

    /**
     * function call for now date with {@link SimpleDateFormat}
     * @param format simple date format
     * @return the result
     */
    @FunctionInvocation
    public String date(final String format) {
        return new SimpleDateFormat(format).format(new java.util.Date());
    }

    /**
     * random date between begin date and end date
     * @param beginDate date with the dd-MM-yyyy HH:mm:ss format
     * @param endDate date with the dd-MM-yyyy HH:mm:ss format
     * @param format output format in {@link SimpleDateFormat} format
     * @return formatted date
     */
    @FunctionInvocation
    public String date(final String beginDate, final String endDate, final String format) {
        try {
            DateFormat formatter = new SimpleDateFormat(DEFAULT_INPUT_FORMAT);
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            Calendar cal = Calendar.getInstance();
            cal.setTime(formatter.parse(beginDate));
            Long beginLong = cal.getTimeInMillis();
            cal.setTime(formatter.parse(endDate));
            Long endLong = cal.getTimeInMillis();
            long randomLong = (long) (beginLong + Math.random() * (endLong - beginLong));
            cal.setTimeInMillis(randomLong);
            return dateFormat.format(new java.util.Date(randomLong));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

    }

    /**
     *
     * @param beginDate date with the dd-MM-yyyy HH:mm:ss format
     * @param endDate date with the dd-MM-yyyy HH:mm:ss format
     * @return formated date in EEE, d MMM yyyy HH:mm:ss z format
     */
    @FunctionInvocation
    public String date(final String beginDate, final String endDate) {
        return date(beginDate, endDate, DEFAULT_DATE_STRING);
    }

    private SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat(DEFAULT_DATE_STRING);
    }



}
