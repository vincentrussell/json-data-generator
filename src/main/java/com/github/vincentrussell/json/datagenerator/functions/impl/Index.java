package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * an incrementing index integer
 */
@Function(name = "index")
public class Index {

    static final Map<String, IndexHolder>
        STRING_INDEX_HOLDER_MAP = new ConcurrentHashMap<>();


    static final String DEFAULT = "DEFAULT";

    /**
     * function call with default index name
     * @return the result
     */
    @FunctionInvocation
    public String getIndex() {
        return getIndex(DEFAULT);
    }

    /**
     * index for given index name
     * @param indexName name of index
     * @return the result
     */
    @FunctionInvocation
    public String getIndex(final String indexName) {
        try {
            return getIndex(DEFAULT, Integer.parseInt(indexName));
        } catch (NumberFormatException e) {
            return "" + getIndexHolder(indexName).getNextIndex();
        }
    }

    /**
     * index for index name
     * @param indexName name of index
     * @param startingPoint starting point integer for index
     * @return the result
     */
    @FunctionInvocation
    public String getIndex(final String indexName, final String startingPoint) {
        return getIndex(indexName, Integer.parseInt(startingPoint));
    }

    private String getIndex(final String indexName, final int startingPoint) {
        return "" + getIndexHolder(indexName, startingPoint).getNextIndex();
    }

    private IndexHolder getIndexHolder(final String indexName) {
        return getIndexHolder(indexName, 0);
    }

    private IndexHolder getIndexHolder(final String indexName, final int startingPoint) {
        if (STRING_INDEX_HOLDER_MAP.containsKey(indexName)) {
            return STRING_INDEX_HOLDER_MAP.get(indexName);
        }
        IndexHolder indexHolder = new IndexHolder(startingPoint);
        STRING_INDEX_HOLDER_MAP.put(indexName, indexHolder);
        return indexHolder;
    }
}
