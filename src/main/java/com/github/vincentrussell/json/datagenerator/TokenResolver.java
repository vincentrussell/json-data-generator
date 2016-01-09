package com.github.vincentrussell.json.datagenerator;


import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;

/**
 * interface for processing functions in the test data
 */
public interface TokenResolver {

    /**
     * process text in json and run return the results of processing the json tokens
     *
     * @param indexHolder
     * @param s
     * @return the result of resolving the token
     */
    String resolveToken(IndexHolder indexHolder, CharSequence s);
}
