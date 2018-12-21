package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;

/**
 * reset an index
 */
@Function(name = "resetIndex")
public class ResetIndex {

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
        if (Index.STRING_INDEX_HOLDER_MAP.containsKey(indexName)) {
            return Index.STRING_INDEX_HOLDER_MAP.get(indexName);
        }
        throw new IllegalStateException("could not find index with name " + indexName);
    }
}
