package com.github.vincentrussell.json.datagenerator.functions;

import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;
import org.bitstrings.test.junit.runner.ClassLoaderPerTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@RunWith(ClassLoaderPerTestRunner.class)
public class ObjectRegistryTest {

    ObjectRegistry objectRegistry;

    @Test
    public void registerAndGetObject() {
        objectRegistry = ObjectRegistry.getInstance();
        IndexHolder indexHolder = new IndexHolder();
        objectRegistry.register(IndexHolder.class, indexHolder);
        IndexHolder indexHolder1 = objectRegistry.getInstance(IndexHolder.class);
        assertSame(indexHolder, indexHolder1);
    }

    @Test
    public void registerAndGetObjectNotFound() {
        objectRegistry = ObjectRegistry.getInstance();
        assertNull(objectRegistry.getInstance(IndexHolder.class));
    }

}
