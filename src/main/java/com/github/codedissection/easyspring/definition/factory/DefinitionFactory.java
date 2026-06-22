package com.github.codedissection.easyspring.definition.factory;

import com.github.codedissection.easyspring.definition.dto.TypeMetadataContainer;
import com.github.codedissection.easyspring.definition.BeanDefinition;
import com.github.codedissection.easyspring.definition.exception.CircularDependenciesException;
import com.github.codedissection.easyspring.definition.exception.MissingImplementationException;
import com.github.codedissection.easyspring.definition.exception.MultipleImplementationException;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefinitionFactory {

    public Map<Class<?>, BeanDefinition> createBeanDefinitions(List<TypeMetadataContainer> containers) {
        var definitionMap = new HashMap<Class<?>, BeanDefinition>();
        var ascendants = getTypeAscendantsIndex(containers);
        var descendants = getTypeDescendantsIndex(ascendants);
        var resolvedImplementationIndex = getResolvedImplementationIndex(descendants);
        for (TypeMetadataContainer container : containers) {
            var sourceClass = container.getSourceClass();
            var rawDependencies = container.getDependencies();
            var dependencies = rawDependencies.stream()
                    .<Class<?>>map(resolvedImplementationIndex::get)
                    .toList();
            var beanDefinition = new BeanDefinition.Builder(sourceClass, dependencies)
                    .build();
            definitionMap.put(sourceClass, beanDefinition);
        }
        return definitionMap;
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
            if (bd == null)
                throw new MissingImplementationException("No implementation found for type " + clazz);
            if (greyStatus.contains(bd))
                throw new CircularDependenciesException("Phase 1. Circular dependency detected.");
            dfs(bd,
                beanDefinitionMap,
                orderedDefinitions,
                greyStatus);
        }
        orderedDefinitions.add(beanDefinition);
        greyStatus.remove(beanDefinition);
    }

    private Map<Class<?>, Class<?>> getResolvedImplementationIndex(Map<Class<?>, Set<Class<?>>> descendants) {
        var resolvedImplementationIndex = new HashMap<Class<?>, Class<?>>();
        var rawImplementationIndex = new HashMap<Class<?>, Set<Class<?>>>();
        for (Map.Entry<Class<?>, Set<Class<?>>> entry : descendants.entrySet()) {
            var rawImplementations = entry.getValue().stream()
                    .filter(type -> !type.isInterface() && !Modifier.isAbstract(type.getModifiers()))
                    .collect(Collectors.toSet());
            if (rawImplementations.size() > 1) //TODO add primary mechanism next time :)
                throw new MultipleImplementationException("Detected more than one implementation for type " + entry.getKey());
            if (rawImplementations.isEmpty())
                throw new MissingImplementationException("No detected implementation for type " + entry.getKey());
            rawImplementationIndex.put(entry.getKey(), rawImplementations);
        }
        for (Map.Entry<Class<?>, Set<Class<?>>> entry : rawImplementationIndex.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue().iterator().next();
            resolvedImplementationIndex.put(key, value);
        }
        return resolvedImplementationIndex;
    }

    private Map<Class<?>, Set<Class<?>>> getTypeDescendantsIndex(Map<Class<?>, Set<Class<?>>> typeLineage) {
        var typeDependencyImplMap = new ConcurrentHashMap<Class<?>, Set<Class<?>>>();
        for (Map.Entry<Class<?>, Set<Class<?>>> entry : typeLineage.entrySet()) {
            var type = entry.getKey();
            var ascendants = entry.getValue();
            for (Class<?> ascendant : ascendants) {
                if (typeDependencyImplMap.containsKey(ascendant)) {
                    typeDependencyImplMap.get(ascendant).add(type);
                } else {
                    Set<Class<?>> set = ConcurrentHashMap.newKeySet();
                    set.add(type);
                    typeDependencyImplMap.put(ascendant, set);
                }
            }
        }
        return typeDependencyImplMap;
    }

    private Map<Class<?>, Set<Class<?>>> getTypeAscendantsIndex(List<TypeMetadataContainer> projectTypes) {
        Map<Class<?>, Set<Class<?>>> typeLineageMap = new HashMap<>();
        for (TypeMetadataContainer container : projectTypes) {
            Class<?> currentNode = container.getSourceClass();
            var currentNodeAndAncestorTypes = findTypeLineage(currentNode);
            typeLineageMap.put(currentNode, currentNodeAndAncestorTypes);
        }
        return typeLineageMap;
    }

    //TODO Исключить дублирование прохода по одним и тем же интерфейсам
    private Set<Class<?>> findTypeLineage(Class<?> currentType) {
        var allAncestors = new HashSet<Class<?>>();
        var directAncestors = new HashSet<Class<?>>();

        if (currentType == null || currentType == Object.class) {
            return allAncestors;
        }

        var superClass = currentType.getSuperclass();
        var superInterfaces = Arrays.asList(currentType.getInterfaces());
        if (superClass != null && superClass != Object.class)
            directAncestors.add(superClass);
        directAncestors.addAll(superInterfaces);

        for (Class<?> clazz : directAncestors) {
            var currentFrameAncestors = findTypeLineage(clazz);
            allAncestors.addAll(currentFrameAncestors);
            allAncestors.add(clazz);
        }
        allAncestors.add(currentType);
        return allAncestors;
    }
}