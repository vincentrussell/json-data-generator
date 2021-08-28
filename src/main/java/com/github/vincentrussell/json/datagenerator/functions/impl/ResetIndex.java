package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;

import java.util.Map;

/**
 * reset an index
 */
@Function(name = "resetIndex")
public class ResetIndex {

    private final FunctionRegistry functionRegistry;

    public ResetIndex(final FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }


    /**
     * reset the default index
     * @return empty string
     */
    @FunctionInvocation
    public String resetIndex() {
        return resetIndex(Index.DEFAULT);
    }

    /**
     * reset index for given index name
     * @param indexName name of index
     * @return empty string
     */
    @FunctionInvocation
    public String resetIndex(final String indexName) {
        IndexHolder indexHolder = getIndexHolder(indexName);
        indexHolder.resetIndex();
        return "";
    }


    private IndexHolder getIndexHolder(final String indexName) {
        Map<String, IndexHolder> stringIndexHolderMap = functionRegistry.getStringIndexHolderMap();
        if (stringIndexHolderMap.containsKey(indexName)) {
            return stringIndexHolderMap.get(indexName);
        }
        throw new IllegalStateException("could not find index with name " + indexName);
    }
}
