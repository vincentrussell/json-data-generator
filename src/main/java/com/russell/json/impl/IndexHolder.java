package com.russell.json.impl;

public class IndexHolder {
    int index = 0;

    public IndexHolder() {}

    public int getNextIndex() {
        return index++;
    }
}
