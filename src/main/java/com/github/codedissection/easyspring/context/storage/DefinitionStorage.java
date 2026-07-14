package com.github.codedissection.easyspring.context.storage;

import com.github.codedissection.easyspring.definition.model.BeanDefinition;

import java.util.Map;

public class DefinitionStorage {
    private volatile Map<Class<?>, BeanDefinition> beanDefinitions;

    public void saveBeanDefinitions(Map<Class<?>, BeanDefinition> beanDefinitions) {
        this.beanDefinitions = Map.copyOf(beanDefinitions);
    }

    public BeanDefinition getBeanDefinition(Class<?> clazz) {
        return beanDefinitions.get(clazz);
    }

}
