package com.github.vincentrussell.json.datagenerator.impl;

import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class FunctionTokenResolverTest {

    FunctionTokenResolver functionTokenResolver;
    FunctionRegistry functionRegistry;

    @Before
    public void setup() {
        functionRegistry = new FunctionRegistry();
        functionTokenResolver = new FunctionTokenResolver(functionRegistry);
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