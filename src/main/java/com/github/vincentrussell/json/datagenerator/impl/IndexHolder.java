package com.github.vincentrussell.json.datagenerator.impl;

/**
 * class used to support auto-incrementing index function in test data json.
 */
public class IndexHolder {
    private int index;

    /**
     * default constructor with index starting from 0.
     */
    public IndexHolder() {
        this(0);
    }

    /**
     * index holder with index starting with another number.
     * @param startingIndex index to start from
     */
    public IndexHolder(final int startingIndex) {
        this.index = startingIndex;
    }

    /**
     * get next index.
     *
     * @return the next index
     */
    public int getNextIndex() {
        return index++;
    }
}
