package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * addHours to Date function
 */
@Function(name = "addHours")
public class AddHours extends AbstractAddDates {

    /**
     * add hours to date
     * @param format the date format
     * @param date the date
     * @param hours the number of hours to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addHours(final String format, final String date, final String hours) {
       return super.addInterval(format, date, hours);
    }

    /**
     * add hours to the date
     * @param date the date
     * @param hours the number of hours to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addHours(final String date, final String hours) {
       return super.addInterval(date, hours);
    }

    @Override
    protected final String getMethodName() {
        return "addHours";
    }
}
