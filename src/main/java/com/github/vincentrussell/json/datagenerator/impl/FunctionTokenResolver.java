package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.TokenResolver;
import com.github.vincentrussell.json.datagenerator.parser.FunctionParser;

import java.io.ByteArrayInputStream;

public class FunctionTokenResolver implements TokenResolver {

    @Override
    public String resolveToken(CharSequence s) {
        try {
            FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream(s.toString().getBytes()));
            return functionParser.Parse();
        } catch (Throwable e) {
            throw new IllegalArgumentException(new StringBuilder("cannot parse function: ").append(s).toString(),e);
        }

    }
}
