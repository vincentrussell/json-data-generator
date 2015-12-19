package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.TokenResolver;
import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.github.vincentrussell.json.datagenerator.functions.ObjectRegistry;
import com.github.vincentrussell.json.datagenerator.parser.FunctionParser;
import com.github.vincentrussell.json.datagenerator.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionTokenResolver implements TokenResolver {

    public static final Pattern FUNCTION_PATTERN = Pattern.compile("\\{\\{(.+)\\}\\}");

    ObjectRegistry objectRegistry = ObjectRegistry.getInstance();

    @Override
    public String resolveToken(IndexHolder indexHolder, CharSequence s) {
        objectRegistry.register(IndexHolder.class,indexHolder);

        Matcher matcher = FUNCTION_PATTERN.matcher(s);

            try {
                if (matcher.matches()) {
                    FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream(matcher.group(1).getBytes()));
                    return functionParser.Parse();
                } else {
                    FunctionParser functionParser = new FunctionParser(new ByteArrayInputStream(s.toString().getBytes()));
                    return functionParser.Parse();
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }

    }


}
