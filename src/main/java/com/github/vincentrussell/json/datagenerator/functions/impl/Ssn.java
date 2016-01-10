package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.text.DecimalFormat;
import java.util.Random;

@Function(name = "ssn")
public class Ssn {

    @FunctionInvocation
    public String ssn() {
        Random rand = new Random();
        int num1 = rand.nextInt(799-1) + 1;
        int num2 = rand.nextInt(99-1) + 1;
        int num3 = rand.nextInt(9999-1) + 1;

        DecimalFormat df1 = new DecimalFormat("000");
        DecimalFormat df2 = new DecimalFormat("00");
        DecimalFormat df3 = new DecimalFormat("0000");

        String ssn = df1.format(num1) + "-" + df2.format(num2) + "-" + df3.format(num3);
        return ssn;
    }

}
