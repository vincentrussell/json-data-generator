package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Random;

/**
 * lowercase hex string
 */
@Function(name = "hex")
public class Hex {

    private final Random random = new Random();

    /**
     * random hex string for random 16 bytes
     * @return the result
     */
    @FunctionInvocation
    @SuppressWarnings("checkstyle:magicnumber")
    public String hex() {
        return getHexString(16);
    }

    /**
     * random hexstring with specified byte size
     * @param byteSize size in bytes of hex string
     * @return the result
     */
    @FunctionInvocation
    public String hex(final String byteSize) {
        return getHexString(Integer.parseInt(byteSize));
    }

    private String getHexString(final int byteSize) {
        byte[]  resBuf = new byte[byteSize];
        random.nextBytes(resBuf);
        StringBuilder sb = new StringBuilder();
        for (byte b : resBuf) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
