package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * addMinutes to Date function
 */
@Function(name = "addMinutes")
public class AddMinutes extends AbstractAddDates {

    /**
     * add minutes to date
     * @param format the date format
     * @param date the date
     * @param minutes the number of minutes to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addMinutes(final String format, final String date, final String minutes) {
       return super.addInterval(format, date, minutes);
    }

    /**
     * add minutes to the date
     * @param date the date
     * @param minutes the number of minutes to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addMinutes(final String date, final String minutes) {
       return super.addInterval(date, minutes);
    }

    @Override
    protected final String getMethodName() {
        return "addMinutes";
    }
}
