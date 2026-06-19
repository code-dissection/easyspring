package com.github.codedissection.easyspring.definition.storage;

import com.github.codedissection.easyspring.definition.BeanDefinition;

import java.util.List;
import java.util.Map;

public class DefinitionStorage {
    private volatile Map<Class<?>, BeanDefinition> beanDefinitions;
    private volatile List<BeanDefinition> sortedDefinitions;

    public void saveBeanDefinitions(Map<Class<?>, BeanDefinition> beanDefinitions, List<BeanDefinition> sortedDefinitions) {
        this.beanDefinitions = Map.copyOf(beanDefinitions);
        this.sortedDefinitions = List.copyOf(sortedDefinitions);
    }

    public Map<Class<?>, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    public List<BeanDefinition> getSortedDefinitions() {
        return sortedDefinitions;
    }

}
