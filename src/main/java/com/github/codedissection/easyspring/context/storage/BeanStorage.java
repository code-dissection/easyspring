package com.github.codedissection.easyspring.context.storage;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanStorage {

    private final ConcurrentHashMap<Class<?>, Object> beanStorage = new ConcurrentHashMap<>();

    public void saveBeans(Map<Class<?>, Object> beanMap) {
        beanStorage.putAll(
                Collections.unmodifiableMap(beanMap)
        );
    }

}
