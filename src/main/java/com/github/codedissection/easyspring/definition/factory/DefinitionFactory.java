package com.github.codedissection.easyspring.definition.factory;

import com.github.codedissection.easyspring.definition.BeanDefinition;
import com.github.codedissection.easyspring.definition.exception.MissingImplementationException;
import com.github.codedissection.easyspring.definition.exception.MultipleImplementationException;
import com.github.codedissection.easyspring.projectscanner.dto.Metadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefinitionFactory {

    public LinkedHashMap<Class<?>, BeanDefinition> createSortedBeanDefinitionMap(List<Metadata> containers) {
        var definitionMap = new LinkedHashMap<Class<?>, BeanDefinition>();
        var resolvedTypes = getResolvedTypes(containers);
        for (Metadata container : containers) {
            var sourceClass = container.getSourceClass();
            var dependencies = container.getDependencies().stream()
                    .<Class<?>>map(rawType -> {
                        var resolvedType = resolvedTypes.get(rawType);
                        if (resolvedType == null)
                            throw new MissingImplementationException("There is no resolved implementation for type " + rawType);
                        return resolvedType;
                    })
                    .toList();
            var beanDefinition = new BeanDefinition.Builder(sourceClass, dependencies)
                    .build();
            definitionMap.put(sourceClass, beanDefinition);
        }
        return definitionMap;
    }

    private Map<Class<?>, Class<?>> getResolvedTypes(List<Metadata> containers) {
        var flattenHierarchy = new LinkedHashMap<Class<?>, Class<?>>();
        for (Metadata container : containers) {
            var clazz = container.getSourceClass();
            var ancestors = getAllClassAncestors(clazz);
            flattenHierarchy.put(clazz, clazz);
            for (Class<?> ancestor : ancestors) {
                if (flattenHierarchy.putIfAbsent(ancestor, clazz) != null) {
                    throw new MultipleImplementationException("Not unique impl for type " + ancestor.getName());
                }
            }
        }
        return flattenHierarchy;
    }

    private Set<Class<?>> getAllClassAncestors(Class<?> clazz) {
        var ancestors = new HashSet<Class<?>>();
        dfs(clazz, ancestors);
        return ancestors;
    }

    private void dfs(Class<?> clazz, Set<Class<?>> ancestors) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        var directAncestors = new HashSet<Class<?>>();
        var superClass = clazz.getSuperclass();
        var superInterfaces = Arrays.asList(clazz.getInterfaces());

        if (!superInterfaces.isEmpty()) {
            directAncestors.addAll(superInterfaces);
        }
        if (superClass != null && superClass != Object.class) {
            directAncestors.add(superClass);
        }

        for (Class<?> ancestor : directAncestors) {
            if (ancestors.add(ancestor)) {
                dfs(ancestor, ancestors);
            }
        }
    }
}