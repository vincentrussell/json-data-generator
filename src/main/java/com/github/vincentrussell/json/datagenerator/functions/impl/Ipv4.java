package com.github.vincentrussell.json.datagenerator.functions.impl;

import java.util.Random;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

@Function(name = "ipv4")
public class Ipv4 {

    @FunctionInvocation
    public String ipv4() {
        Random rand = new Random();
        return rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256);
    }

}
