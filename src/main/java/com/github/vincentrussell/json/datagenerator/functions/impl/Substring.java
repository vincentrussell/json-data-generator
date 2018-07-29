package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * get substring from other string
 */
@Function(name = "substring")
public class Substring {

    /**
     * Returns a string that is a substring of this string. The
     * substring begins with the character at the specified index and
     * extends to the end of this string. <p>
     * Examples:
     * <blockquote><pre>
     * "unhappy".substring(2) returns "happy"
     * "Harbison".substring(3) returns "bison"
     * "emptiness".substring(9) returns "" (an empty string)
     * </pre></blockquote>
     * @param value string
     * @param beginIndex  the beginning index, inclusive.
     * @return the result
     */
    @FunctionInvocation
    public String substring(final String value, final String beginIndex) {
        return value.substring(Integer.valueOf(beginIndex));
    }

    /**
     * Returns a string that is a substring of this string. The
     * substring begins at the specified {@code beginIndex} and
     * extends to the character at index {@code endIndex - 1}.
     * Thus the length of the substring is {@code endIndex-beginIndex}.
     * <p>
     * Examples:
     * <blockquote><pre>
     * "hamburger".substring(4, 8) returns "urge"
     * "smiles".substring(1, 5) returns "mile"
     * </pre></blockquote>
     * @param value the string
     * @param beginIndex the beginning index, inclusive.
     * @param endIndex the ending index, exclusive
     * @return the result
     */
    @FunctionInvocation
    public String substring(final String value, final String beginIndex, final String endIndex) {
        return value.substring(Integer.valueOf(beginIndex), Integer.valueOf(endIndex));
    }
}
