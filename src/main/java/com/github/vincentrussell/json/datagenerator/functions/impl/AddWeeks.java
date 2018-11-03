package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * addWeeks to Date function
 */
@Function(name = "addWeeks")
public class AddWeeks extends AbstractAddDates {

    /**
     * add weeks to date
     * @param format the date format
     * @param date the date
     * @param weeks the number of weeks to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addWeeks(final String format, final String date, final String weeks) {
       return super.addInterval(format, date, weeks);
    }

    /**
     * add weeks to the date
     * @param date the date
     * @param weeks the number of weeks to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addWeeks(final String date, final String weeks) {
       return super.addInterval(date, weeks);
    }

    @Override
    protected final String getMethodName() {
        return "addWeeks";
    }
}
