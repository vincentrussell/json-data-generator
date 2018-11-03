package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * addSeconds to Date function
 */
@Function(name = "addSeconds")
public class AddSeconds extends AbstractAddDates {

    /**
     * add seconds to date
     * @param format the date format
     * @param date the date
     * @param seconds the number of seconds to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addSeconds(final String format, final String date, final String seconds) {
       return super.addInterval(format, date, seconds);
    }

    /**
     * add seconds to the date
     * @param date the date
     * @param seconds the number of seconds to add
     * @return the new date
     */
    @FunctionInvocation
    public final String addSeconds(final String date, final String seconds) {
       return super.addInterval(date, seconds);
    }

    @Override
    protected final String getMethodName() {
        return "addSeconds";
    }
}
