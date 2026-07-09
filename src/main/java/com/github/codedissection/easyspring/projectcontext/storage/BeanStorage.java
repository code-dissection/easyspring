package com.github.codedissection.easyspring.projectcontext.storage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class BeanStorage {

    private final ConcurrentHashMap<Class<?>, Object> beanStorage = new ConcurrentHashMap<>();

    public void saveBeans(LinkedHashMap<Class<?>, Object> beanMap) {
        beanStorage.putAll(
                Collections.unmodifiableMap(beanMap)
        );
    }

}
