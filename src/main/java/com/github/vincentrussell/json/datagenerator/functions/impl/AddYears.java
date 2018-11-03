package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * addYears to Date function
 */
@Function(name = "addYears")
public class AddYears extends AbstractAddDates {

    /**
     * add years to date
     * @param format the date format
     * @param date the date
     * @param years the number of years to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addYears(final String format, final String date, final String years) {
       return super.addInterval(format, date, years);
    }

    /**
     * add years to the date
     * @param date the date
     * @param years the number of years to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addYears(final String date, final String years) {
       return super.addInterval(date, years);
    }

    @Override
    protected final String getMethodName() {
        return "addYears";
    }
}
