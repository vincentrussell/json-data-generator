package com.github.vincentrussell.json.datagenerator.impl;

import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.github.vincentrussell.json.datagenerator.parser.FunctionParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class FunctionTokenResolverTest {

    private static FunctionRegistry FUNCTIONREGISTRY = FunctionRegistry.getInstance();

    FunctionTokenResolver functionTokenResolver;

    @Before
    public void setup() {
        functionTokenResolver = new FunctionTokenResolver();
    }

    @Test
    public void unknownFunction() {
        Assert.assertThrows("expected IllegalArgumentException", IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                functionTokenResolver.resolveToken("someFunction()");

            }
        });
    }


    @Test
    public void badArgumentsForFunction() {
        Assert.assertThrows("expected IllegalArgumentException", IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                functionTokenResolver.resolveToken("integer(\"text\",\"text\")");

            }
        });
    }

}