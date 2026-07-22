package com.github.codedissection.easyspring.context.storage;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanStorage {

    private ConcurrentHashMap<Class<?>, Object> beanStorage = new ConcurrentHashMap<>();

    public void saveBeans(Map<Class<?>, Object> beanMap) {
        beanStorage.putAll(
                Collections.unmodifiableMap(beanMap)
        );
    }

    public void saveBean(Class<?> key, Object bean) {
        beanStorage.put(key, bean);
    }

    public Object getBean(Class<?> clazz) {
        return beanStorage.get(clazz);
    }

    public boolean contains(Class<?> key) {
        return beanStorage.containsKey(key);
    }

    public void clear() {
        beanStorage = null;
    }
}
