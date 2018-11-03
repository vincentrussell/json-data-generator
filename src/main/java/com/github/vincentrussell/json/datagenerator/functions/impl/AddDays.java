package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * addDays to Date function
 */
@Function(name = "addDays")
public class AddDays extends AbstractAddDates {

    /**
     * add days to date
     * @param format the date format
     * @param date the date
     * @param days the number of days to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addDays(final String format, final String date, final String days) {
       return super.addInterval(format, date, days);
    }

    /**
     * add days to the date
     * @param date the date
     * @param days the number of days to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addDays(final String date, final String days) {
       return super.addInterval(date, days);
    }

    @Override
    protected final String getMethodName() {
        return "addDays";
    }
}
