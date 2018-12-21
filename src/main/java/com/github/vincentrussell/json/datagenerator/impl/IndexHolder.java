package com.github.vincentrussell.json.datagenerator.impl;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * class used to support auto-incrementing index function in test data json.
 */
public class IndexHolder {
    private final ResettingMutableInt index;

    /**
     * index holder with index starting with another number.
     * @param startingIndex index to start from
     */
    public IndexHolder(final int startingIndex) {
        this.index = new ResettingMutableInt(startingIndex);
    }

    /**
     * get next index.
     *
     * @return the next index
     */
    public int getNextIndex() {
        return index.getNextNumber();
    }

    /**
     * reset the index
     */
    public void resetIndex() {
        index.reset();
    }

    /**
     * {@link MutableInt} that can be reset
     */
    private static class ResettingMutableInt extends MutableInt {
        private final int startingPoint;

        /**
         * constructor
         * @param startingPoint starting integer
         */
        ResettingMutableInt(final int startingPoint) {
            super(startingPoint);
            this.startingPoint = startingPoint;
        }

        /**
         * reset the index to that starting point
         */
        public void reset() {
            setValue(startingPoint);
        }

        /**
         * get the next number for this index; then increment
         * @return
         */
        public int getNextNumber() {
            try {
                return toInteger();
            } finally {
                increment();
            }
        }
    }
}
