package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

/**
 * random uuid
 */
@Function(name = {"uuid", "guid"})
public class UUID {

    /**
     * random uuid
     * @return the uuid
     */
    @FunctionInvocation
    public String getRandomUUID() {
        return getRandomUUID(Boolean.TRUE.toString());
    }


    /**
     * random hex string for random 16 bytes
     * @param keepDashes false to remove dashes from uuid
     * @return the uuid with out with dashes
     */
    @FunctionInvocation
    @SuppressWarnings("checkstyle:magicnumber")
    public String getRandomUUID(final String keepDashes) {
        String uuid = java.util.UUID.randomUUID().toString();

        if (!Boolean.valueOf(keepDashes)) {
            return uuid.replace("-", "");
        }

        return uuid;
    }
}
