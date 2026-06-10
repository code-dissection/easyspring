package com.github.codedissection.easyspring.bean.resolver;

import com.github.codedissection.easyspring.definition.BeanDefinition;

import java.util.*;

public class BeanDefinitionSorter {

    public List<BeanDefinition> sortBeanDefinitions(Map<String, BeanDefinition> beanDefinitionMap) {
        Set<BeanDefinition> greyStatus = new LinkedHashSet<>();
        List<BeanDefinition> orderedDefinitions = new ArrayList<>();

        for (String key : beanDefinitionMap.keySet()) {
            var bd = beanDefinitionMap.get(key);
            dfs(bd, beanDefinitionMap, orderedDefinitions, greyStatus);
        }
        return orderedDefinitions;
    }

    private void dfs(BeanDefinition beanDefinition,
                     Map<String, BeanDefinition> beanDefinitionMap,
                     List<BeanDefinition> orderedDefinitions,
                     Set<BeanDefinition> greyStatus) {
        if (orderedDefinitions.contains(beanDefinition)) {
            return;
        }
        greyStatus.add(beanDefinition);
        for (Class<?> clazz : beanDefinition.getDependencies()) {
            var bd = beanDefinitionMap.get(clazz.getName());
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
