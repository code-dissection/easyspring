package com.github.codedissection.easyspring.projectcontext.storage;

import com.github.codedissection.easyspring.definition.BeanDefinition;

import java.util.Map;

public class DefinitionStorage {
    private volatile Map<Class<?>, BeanDefinition> beanDefinitions;

    public void saveBeanDefinitions(Map<Class<?>, BeanDefinition> beanDefinitions) {
        this.beanDefinitions = Map.copyOf(beanDefinitions);
    }

    public Map<Class<?>, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

}
