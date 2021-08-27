package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.TokenResolver;
import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.github.vincentrussell.json.datagenerator.parser.FunctionParser;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/**
 * {@link TokenResolver} implementation that will try to run the functions for the tockens
 */
public class FunctionTokenResolver implements TokenResolver {

    private final FunctionRegistry functionRegistry;

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionTokenResolver.class);

    public FunctionTokenResolver(final FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolveToken(final CharSequence s) {
        try {
            FunctionParser functionParser = new FunctionParser(
                new ByteArrayInputStream(s.toString().getBytes(Charsets.UTF_8)), Charsets.UTF_8);
            functionParser.setFunctionRegistry(functionRegistry);
            return functionParser.Parse();
        } catch (Throwable e) {
            LOGGER.warn(e.getMessage(), e);
            throw new IllegalArgumentException(new StringBuilder("cannot parse function: ")
                .append(s).toString(), e);
        }

    }
}
