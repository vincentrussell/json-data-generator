package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Random;

@Function(name = "hex")
public class Hex {

    private final Random random = new Random();

    @FunctionInvocation
    public String hex() {
        return getHexString(16);
    }

    @FunctionInvocation
    public String hex(String byteSize) {
        return getHexString(Integer.parseInt(byteSize));
    }

    private String getHexString(int byteSize) {
        byte[]  resBuf = new byte[byteSize];
        random.nextBytes(resBuf);
        StringBuilder sb = new StringBuilder();
        for (byte b : resBuf) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
