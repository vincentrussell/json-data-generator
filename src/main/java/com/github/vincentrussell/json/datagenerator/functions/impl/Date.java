package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Function(name = "date")
public class Date {

    public static final String DEFAULT_DATE_STRING = "EEE, d MMM yyyy HH:mm:ss z";
    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_STRING);


    @FunctionInvocation
    public String date() {
        return DEFAULT_DATE_FORMAT.format(new java.util.Date());
    }

    @FunctionInvocation
    public String date(String format) {
        return new SimpleDateFormat(format).format(new java.util.Date());
    }

    @FunctionInvocation
    public String date(String beginDate, String endDate, String format) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
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

    @FunctionInvocation
    public String date(String beginDate, String endDate) {
        return date(beginDate, endDate, DEFAULT_DATE_STRING);
    }


}
