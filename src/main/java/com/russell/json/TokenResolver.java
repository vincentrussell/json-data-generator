package com.russell.json;


import com.russell.json.impl.IndexHolder;

public interface TokenResolver {
    String resolveToken(IndexHolder indexHolder, CharSequence s);
}
