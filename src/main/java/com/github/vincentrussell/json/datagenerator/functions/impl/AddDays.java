package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * addDays to Date function
 */
@Function(name = "addDays")
public class AddDays {

    /**
     * add days to date
     * @param format the date format
     * @param date the date
     * @param days the number of days to add
     * @return the new date
     */
    @FunctionInvocation
    public String addDays(final String format, final String date, final String days) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            java.util.Date myDate = dateFormat.parse(date);
            return dateFormat.format(DateUtils.addDays(myDate, Integer.valueOf(days)));
        } catch (ParseException | NullPointerException | NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * add days to the date
     * @param date the date
     * @param days the number of days to add
     * @return the new date
     */
    @FunctionInvocation
    public String addDays(final String date, final String days) {
        return addDays(Date.DEFAULT_INPUT_FORMAT, date, days);
    }

}
