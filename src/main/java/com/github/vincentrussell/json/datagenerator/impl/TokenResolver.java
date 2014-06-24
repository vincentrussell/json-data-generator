package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.impl.impl.IndexHolder;

public interface TokenResolver {
    String resolveToken(IndexHolder indexHolder, CharSequence s);
}
