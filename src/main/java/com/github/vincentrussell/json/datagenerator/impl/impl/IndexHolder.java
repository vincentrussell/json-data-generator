package com.github.vincentrussell.json.datagenerator.impl.impl;

public class IndexHolder {
    int index = 0;

    public IndexHolder() {}

    public int getNextIndex() {
        return index++;
    }
}
