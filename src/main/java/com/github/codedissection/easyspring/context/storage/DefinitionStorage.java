package com.github.codedissection.easyspring.context.storage;

import com.github.codedissection.easyspring.definition.model.BeanDefinition;

import java.util.LinkedHashMap;

public class DefinitionStorage {
    private volatile LinkedHashMap<Class<?>, BeanDefinition> beanDefinitions;

    public void saveBeanDefinitions(LinkedHashMap<Class<?>, BeanDefinition> beanDefinitions) {
        this.beanDefinitions = new LinkedHashMap<>(beanDefinitions);
    }

    public BeanDefinition getBeanDefinition(Class<?> clazz) {
        return beanDefinitions.get(clazz);
    }

    public LinkedHashMap<Class<?>, BeanDefinition> getBeanDefinitions() {
        return new LinkedHashMap<>(beanDefinitions);
    }

    public void clear() {
        beanDefinitions = null;
    }
}
