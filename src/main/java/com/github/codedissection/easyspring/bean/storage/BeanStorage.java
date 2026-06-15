package com.github.codedissection.easyspring.bean.storage;

import com.github.codedissection.easyspring.bean.exception.BeanCreateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanStorage {

    private final ConcurrentHashMap<Class<?>, Object> beanRegistry = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<?>, List<Class<?>>> beanHierarchyRegistry;

    public boolean addBean(Class<?> sourceClass, Object bean) {
        return beanRegistry.putIfAbsent(sourceClass, bean) == null;
    }

    public boolean containsBeanByType(Class<?> type) {
        return beanRegistry.containsKey(type);
    }

    public List<Object> getBeansByType(Class<?> type) {
        var parents = beanHierarchyRegistry.getOrDefault(type, List.of());
        var toReturn = new ArrayList<>();
        for (Class<?> clazz : parents) {
            toReturn.add(beanRegistry.get(clazz));
        }
        return toReturn;
    }

    public boolean addProjectHierarchy(Map<Class<?>, List<Class<?>>> hierarchy) {
        try {
            this.beanHierarchyRegistry = new ConcurrentHashMap<>(hierarchy);
        } catch (Exception e) {
            throw new BeanCreateException("Pipeline phase 2 failed: create beanHierarchyRegistry failed", e);
        }
        return true;
    }

}
