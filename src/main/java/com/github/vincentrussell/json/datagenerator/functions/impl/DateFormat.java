package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * convert dates to different formats
 */
@Function(name = "dateFormat")
public class DateFormat {

    /**
     * function call to change format of date
     * @param dateToParse the date to convert
     * @param inputFormat the {@link SimpleDateFormat} to convert from
     * @param outputFormat the{@link SimpleDateFormat} to convert to
     * @return the converted date
     */
    @FunctionInvocation
    public String dateFormat(final String dateToParse, final String inputFormat,
        final String outputFormat) {
        try {
            java.text.DateFormat incoming = new SimpleDateFormat(inputFormat);
            java.text.DateFormat outgoingFormat = new SimpleDateFormat(outputFormat);
            java.util.Date date = incoming.parse(dateToParse);
            return outgoingFormat.format(date);
        } catch (NullPointerException | ParseException e) {
            throw new IllegalArgumentException(e);
        }

    }




}
