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
        try {
            return getIndex(DEFAULT,Integer.parseInt(indexName));
        } catch (NumberFormatException e) {
            return "" + getIndexHolder(indexName).getNextIndex();
        }
    }

    @FunctionInvocation
    public String getIndex(String indexName, String startingPoint) {
        return getIndex(indexName,Integer.parseInt(startingPoint));
    }

    private String getIndex(String indexName, int startingPoint) {
        return "" + getIndexHolder(indexName,startingPoint).getNextIndex();
    }

    private IndexHolder getIndexHolder(String indexName) {
        return getIndexHolder(indexName,0);
    }

    private IndexHolder getIndexHolder(String indexName, int startingPoint) {
        if (stringIndexHolderMap.containsKey(indexName)) {
            return stringIndexHolderMap.get(indexName);
        }
        IndexHolder indexHolder = new IndexHolder(startingPoint);
        stringIndexHolderMap.put(indexName, indexHolder);
        return indexHolder;
    }
}
