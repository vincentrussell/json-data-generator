package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.impl.impl.IndexHolder;

/**
 * interface for processing functions in the test data
 */
public interface TokenResolver {

    /**
     * process text in json and run it's function specified in FunctionsImpl
     * @param indexHolder
     * @param s
     * @return
     */
    String resolveToken(IndexHolder indexHolder, CharSequence s);
}
