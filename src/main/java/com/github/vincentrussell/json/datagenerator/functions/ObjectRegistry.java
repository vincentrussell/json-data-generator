package com.github.vincentrussell.json.datagenerator.functions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectRegistry {

    private Map<Class, Object> objectMap = new ConcurrentHashMap<>();

    private static ObjectRegistry INSTANCE;

    private ObjectRegistry() {
    }

    public static ObjectRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ObjectRegistry();
        }
        return INSTANCE;
    }

    public void register(Class clazz, Object object) {
        objectMap.put(clazz, object);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {
        Object object = objectMap.get(clazz);
        if (object != null && object.getClass().isAssignableFrom(clazz)) {
            return (T) object;
        }
        return null;
    }
}
