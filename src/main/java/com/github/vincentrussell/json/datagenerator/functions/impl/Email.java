package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = "email")
public class Email {

    private static final FirstName FIRST_NAME = new FirstName();
    private static final LastName LAST_NAME = new LastName();
    private static final Company COMPANY = new Company();

    @FunctionInvocation
    public String email() {
        return FIRST_NAME.firstName().toLowerCase() + "." + LAST_NAME.lastName().toLowerCase() + "@" + COMPANY.company().toLowerCase() + ".com";
    }
    
    @FunctionInvocation
    public String email(String domain) {
        return FIRST_NAME.firstName().toLowerCase() + "." + LAST_NAME.lastName().toLowerCase() + "@" + domain.toLowerCase() + ".com";
    }

}
