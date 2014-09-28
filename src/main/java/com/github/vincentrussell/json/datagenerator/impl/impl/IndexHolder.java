package com.github.vincentrussell.json.datagenerator.impl.impl;

/**
 * class used to support auto-incrementing index function in test data json
 */
public class IndexHolder {
    int index = 0;

    public IndexHolder() {}

    /**
     * get next index
     * @return the next index
     */
    public int getNextIndex() {
        return index++;
    }
}
