package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.text.DecimalFormat;
import java.util.Random;

@Function(name = "ipv4")
public class Ipv4 {

    @FunctionInvocation
    public String ipv4() {
        Random rand = new Random();
        return rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256) + "." + rand.nextInt(256);
    }

}
