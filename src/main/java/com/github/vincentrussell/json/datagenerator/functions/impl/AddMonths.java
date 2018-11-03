package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * addMonths to Date function
 */
@Function(name = "addMonths")
public class AddMonths extends AbstractAddDates {

    /**
     * add months to date
     * @param format the date format
     * @param date the date
     * @param months the number of months to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addMonths(final String format, final String date, final String months) {
       return super.addInterval(format, date, months);
    }

    /**
     * add months to the date
     * @param date the date
     * @param months the number of months to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addMonths(final String date, final String months) {
       return super.addInterval(date, months);
    }

    @Override
    protected final String getMethodName() {
        return "addMonths";
    }
}
