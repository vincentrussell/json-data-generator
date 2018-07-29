package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * random username based on first initial from rnadom first name and lastname lowercased
 */
@Function(name = "username")
public class Username {

    private static final FirstName FIRST_NAME = new FirstName();
    private static final LastName LAST_NAME = new LastName();

    /**
     * random username based on first initial from rnadom first name and lastname lowercased
     * @return the result
     */
    @FunctionInvocation
    public String username() {
        String firstname = FIRST_NAME.firstName();
        String lastname = LAST_NAME.lastName();

        StringBuilder username = new StringBuilder();

        username.append(firstname.substring(0, 1).toLowerCase());
        username.append(lastname.toLowerCase());
        return username.toString();
    }
}
