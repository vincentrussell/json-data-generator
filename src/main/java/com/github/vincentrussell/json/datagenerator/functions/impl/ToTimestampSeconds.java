package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * lower case a string
 */
@Function(name = "toTimestampSeconds")
public class ToTimestampSeconds {

    public static final int ONE_THOUSAND_MILLISECONDS = 1000;
    public static final String DEFAULT_DATE_STRING = "EEE, d MMM yyyy HH:mm:ss z";

    /**
     * convert date string to timestamp
     * @param string
     * @return the timestamp of that string
     */
    @FunctionInvocation
    public String toTimestampSeconds(final String string) {
        return toTimestampSeconds(string, DEFAULT_DATE_STRING);
    }


    /**
     * convert date string to timestamp
     * @param string
     * @param format
     * @return the timestamp of that string
     */
    @FunctionInvocation
    public String toTimestampSeconds(final String string, final String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(formatter.parse(string));
            Long timeInMillis = cal.getTimeInMillis();
            return "" + (timeInMillis / ONE_THOUSAND_MILLISECONDS);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
