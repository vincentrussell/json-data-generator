package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.TokenResolver;
import com.github.vincentrussell.json.datagenerator.parser.FunctionParser;
import com.google.common.base.Charsets;

import java.io.ByteArrayInputStream;

/**
 * {@link TokenResolver} implementation that will try to run the functions for the tockens
 */
public class FunctionTokenResolver implements TokenResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolveToken(final CharSequence s) {
        try {
            FunctionParser functionParser = new FunctionParser(
                new ByteArrayInputStream(s.toString().getBytes(Charsets.UTF_8)), Charsets.UTF_8);
            return functionParser.Parse();
        } catch (Throwable e) {
            throw new IllegalArgumentException(new StringBuilder("cannot parse function: ")
                .append(s).toString(), e);
        }

    }
}
