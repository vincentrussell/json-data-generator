package com.github.vincentrussell.json.datagenerator.impl;

/**
 * class used to support auto-incrementing index function in test data json
 */
public class IndexHolder {
    int index;

    public IndexHolder() {
        this(0);
    }

    public IndexHolder(int startingIndex) {
        this.index = startingIndex;
    }

    /**
     * get next index
     *
     * @return the next index
     */
    public int getNextIndex() {
        return index++;
    }
}
