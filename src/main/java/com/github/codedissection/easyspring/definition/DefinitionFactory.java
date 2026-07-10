package com.github.codedissection.easyspring.definition;

import com.github.codedissection.easyspring.definition.beandefinition.BeanDefinition;
import com.github.codedissection.easyspring.definition.exception.MissingImplementationException;
import com.github.codedissection.easyspring.definition.exception.MultipleImplementationException;
import com.github.codedissection.easyspring.scanner.dto.Metadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.codedissection.easyspring.definition.exception.message.MessageTemplate.MULTIPLE_IMPLEMENTATIONS_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.definition.exception.message.MessageTemplate.RESOLVED_IMPLEMENTATION_ERROR_TEMPLATE;

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
                            throw new MissingImplementationException(String.format(
                                    RESOLVED_IMPLEMENTATION_ERROR_TEMPLATE,
                                    rawType
                            ));
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
            var child = container.getSourceClass();
            var ancestors = getAllClassAncestors(child);
            flattenHierarchy.put(child, child);
            for (Class<?> ancestor : ancestors) {
                var alreadyExistChild = flattenHierarchy.get(ancestor);
                if (alreadyExistChild != null) {
                    throw new MultipleImplementationException(String.format(
                            MULTIPLE_IMPLEMENTATIONS_ERROR_TEMPLATE,
                            ancestor,
                            child,
                            alreadyExistChild
                    ));
                }
                flattenHierarchy.put(ancestor, child);
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