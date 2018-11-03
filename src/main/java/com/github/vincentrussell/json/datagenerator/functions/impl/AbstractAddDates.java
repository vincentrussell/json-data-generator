package com.github.vincentrussell.json.datagenerator.functions.impl;

import org.apache.commons.lang.time.DateUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * abstract add time intervals to Date function
 */
public abstract class AbstractAddDates {

    private Method method;

    /**
     * call this method from the subclasses to add an interval to a date
     * @param format the date format
     * @param date the date
     * @param interval the interval to add (or subtract)
     * @return
     */
    protected String addInterval(final String format, final String date, final String interval) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            java.util.Date myDate = dateFormat.parse(date);
            return dateFormat.format(addInterval(myDate, Integer.valueOf(interval)));
        } catch (ParseException | NullPointerException | NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * call this method from the subclasses to add an interval to a date
     * @param date the date
     * @param interval the interval to add (or subtract)
     * @return
     */
    protected String addInterval(final String date, final String interval) {
        return addInterval(Date.DEFAULT_INPUT_FORMAT, date, interval);
    }


    private synchronized Method getMethod() throws NoSuchMethodException {
        return DateUtils.class.getMethod(getMethodName(), java.util.Date.class, int.class);
    }

    private java.util.Date addInterval(final java.util.Date oldDate, final Integer amount) {
        try {
            if (method == null) {
                method = getMethod();
                method.setAccessible(true);
            }
            return (java.util.Date) method.invoke(null, oldDate, amount);
        } catch (IllegalAccessException | NoSuchMethodException
            | SecurityException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * override this method to indicate with method on {@link DateUtils} could be called
     * @return the method name on {@link DateUtils}
     */
    protected abstract String getMethodName();

}
