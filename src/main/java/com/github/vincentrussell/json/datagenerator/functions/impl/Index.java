package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;

import java.util.Map;

/**
 * an incrementing index integer
 */
@Function(name = "index")
public class Index {

    static final String DEFAULT = "DEFAULT";

    private final FunctionRegistry functionRegistry;

    public Index(final FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

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
        Map<String, IndexHolder> stringIndexHolderMap = functionRegistry.getStringIndexHolderMap();
        if (stringIndexHolderMap.containsKey(indexName)) {
            return stringIndexHolderMap.get(indexName);
        }
        IndexHolder indexHolder = new IndexHolder(startingPoint);
        stringIndexHolderMap.put(indexName, indexHolder);
        return indexHolder;
    }
}
