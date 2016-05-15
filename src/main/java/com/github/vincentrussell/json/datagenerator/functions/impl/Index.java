package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;
import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Function(name = "index")
public class Index {

    private static Map<String,IndexHolder> stringIndexHolderMap = new ConcurrentHashMap<>();


    public static final String DEFAULT = "DEFAULT";

    @FunctionInvocation
    public String getIndex() {
        return getIndex(DEFAULT);
    }

    @FunctionInvocation
    public String getIndex(String indexName) {
        return "" + getIndexHolder(indexName).getNextIndex();
    }

    private IndexHolder getIndexHolder(String indexName) {
        if (stringIndexHolderMap.containsKey(indexName)) {
            return stringIndexHolderMap.get(indexName);
        }
        IndexHolder indexHolder = new IndexHolder();
        stringIndexHolderMap.put(indexName, indexHolder);
        return indexHolder;
    }
}
