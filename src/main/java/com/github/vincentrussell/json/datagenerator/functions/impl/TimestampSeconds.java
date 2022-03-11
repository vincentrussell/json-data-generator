package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * timestamp (seconds, between the current time and midnight, January 1, 1970 UTC):
 */
@Function(name = "timestampSeconds")
public class TimestampSeconds {

    public static final int ONE_THOUSAND_MILLISECONDS = 1000;

    /**
     * timestamp based on "now"
     * @return the result
     */
    @FunctionInvocation
    public String timestampSeconds() {
        return (new java.util.Date().getTime() / ONE_THOUSAND_MILLISECONDS) + "";
    }

    /**
     * random timestamp in seconds between two dates
     * @param beginDate beginning date in format "dd-MM-yyyy HH:mm:ss"
     * @param endDate ending date in format "dd-MM-yyyy HH:mm:ss"
     * @return the result
     */
    @FunctionInvocation
    public String timestampSeconds(final String beginDate, final String endDate) {
        try {
            DateFormat formatter = new SimpleDateFormat(Date.DEFAULT_INPUT_FORMAT);
            Calendar cal = Calendar.getInstance();
            cal.setTime(formatter.parse(beginDate));
            Long beginLong = cal.getTimeInMillis();
            cal.setTime(formatter.parse(endDate));
            Long endLong = cal.getTimeInMillis();
            long randomLong = (long) (beginLong + Math.random() * (endLong - beginLong));
            return (randomLong / ONE_THOUSAND_MILLISECONDS) + "";
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

    }

}
