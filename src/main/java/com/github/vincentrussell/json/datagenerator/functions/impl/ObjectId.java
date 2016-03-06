package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = "objectId")
public class ObjectId {

    private static final java.util.Random RANDOM = new java.util.Random();
    final protected static char[] hexArray = "0123456789abcdef".toCharArray();

    @FunctionInvocation
    public String getObjectId() {
        byte[] bytes = new byte[12];
        RANDOM.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
