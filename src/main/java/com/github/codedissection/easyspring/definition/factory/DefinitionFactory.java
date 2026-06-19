package com.github.codedissection.easyspring.definition.factory;

import com.github.codedissection.easyspring.definition.dto.TypeMetadataContainer;
import com.github.codedissection.easyspring.definition.BeanDefinition;

import java.lang.reflect.Modifier;
import java.util.*;

public class DefinitionFactory {

    public Map<Class<?>, BeanDefinition> createBeanDefinitions(List<TypeMetadataContainer> types) {
        Map<Class<?>, BeanDefinition> definitionStorage = new HashMap<>();
        for (TypeMetadataContainer type : types) {
            var sourceClass = type.getSourceClass();
            if (sourceClass.isInterface() || Modifier.isAbstract(sourceClass.getModifiers()))
                continue;
            var dependencies = type.getDependencies();
            var bd = new BeanDefinition.Builder(sourceClass, dependencies)
                    .build();
            definitionStorage.put(sourceClass, bd);
        }
        return definitionStorage;
    }

    public List<BeanDefinition> sortBeanDefinitions(Map<Class<?>, BeanDefinition> beanDefinitionMap) {
        Set<BeanDefinition> greyStatus = new LinkedHashSet<>();
        List<BeanDefinition> orderedDefinitions = new ArrayList<>();

        for (Class<?> key : beanDefinitionMap.keySet()) {
            var bd = beanDefinitionMap.get(key);
            dfs(bd, beanDefinitionMap, orderedDefinitions, greyStatus);
        }
        return orderedDefinitions;
    }

    private void dfs(BeanDefinition beanDefinition,
                     Map<Class<?>, BeanDefinition> beanDefinitionMap,
                     List<BeanDefinition> orderedDefinitions,
                     Set<BeanDefinition> greyStatus) {
        if (orderedDefinitions.contains(beanDefinition)) {
            return;
        }
        greyStatus.add(beanDefinition);
        for (Class<?> clazz : beanDefinition.getDependencies()) {
            var bd = beanDefinitionMap.get(clazz);
            if (greyStatus.contains(bd)) {
                throw new RuntimeException();
            }
            dfs(bd,
                beanDefinitionMap,
                orderedDefinitions,
                greyStatus);
        }
        orderedDefinitions.add(beanDefinition);
        greyStatus.remove(beanDefinition);
    }
}