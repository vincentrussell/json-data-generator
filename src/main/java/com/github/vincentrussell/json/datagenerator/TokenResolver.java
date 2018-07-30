package com.github.vincentrussell.json.datagenerator;


/**
 * interface for processing functions in the test data.
 */
public interface TokenResolver {

    /**
     * process text in json and run return the results of processing the json tokens.
     *
     * @param string the string
     * @return the result of resolving the token
     */
    String resolveToken(CharSequence string);
}
