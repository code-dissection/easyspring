package com.github.codedissection.easyspring.topologysorter;

import com.github.codedissection.easyspring.scanner.model.TypeMetadata;
import com.github.codedissection.easyspring.topologysorter.enums.State;
import com.github.codedissection.easyspring.topologysorter.exception.CircularDependencyException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.codedissection.easyspring.topologysorter.exception.message.MessageTemplate.CIRCULAR_DEPENDENCY_ERROR_TEMPLATE;

public class MetadataTopologySorter {

    public List<TypeMetadata> getSortedMetadata(Set<TypeMetadata> containers) {
        var sorted = new ArrayList<TypeMetadata>();
        var nodeState = new HashMap<State, Set<Class<?>>>();
        nodeState.put(State.GREY, new HashSet<>());
        nodeState.put(State.BLACK, new HashSet<>());
        var registry = new HashMap<Class<?>, TypeMetadata>();
        for (TypeMetadata container : containers) {
            registry.put(container.sourceClass(), container);
        }
        for (TypeMetadata container : containers) {
            var clazz = container.sourceClass();
            dfs(clazz, sorted, nodeState, registry);
        }
        return sorted;
    }

    private void dfs(Class<?> clazz, List<TypeMetadata> sorted, Map<State, Set<Class<?>>> nodeState, Map<Class<?>, TypeMetadata> registry) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        if (clazz.isInterface()) {
            for (Map.Entry<Class<?>, TypeMetadata> entrySet : registry.entrySet()) {
                if (clazz.isAssignableFrom(entrySet.getKey())) {
                    clazz = entrySet.getKey();
                    break;
                }
            }
        }

        if (nodeState.get(State.BLACK).contains(clazz)) {
            return;
        }

        if (nodeState.get(State.GREY).contains(clazz)) {
            var metadataList = registry.entrySet().stream()
                    .filter(keyValue -> nodeState.get(State.GREY).contains(keyValue.getKey()))
                    .map(Map.Entry::getValue)
                    .toList();

            for (TypeMetadata metadata : metadataList) {
                Class<?> finalClazz = clazz;
                var shouldIStop = metadata.dependencies().stream()
                        .anyMatch(dependency -> dependency.isAssignableFrom(finalClazz));
                if (shouldIStop) {
                    throw new CircularDependencyException(String.format(
                            CIRCULAR_DEPENDENCY_ERROR_TEMPLATE,
                            clazz.getName(),
                            metadata.sourceClass().getName()
                    ));
                }
            }
        }

        var metadata = registry.get(clazz);
        List<Class<?>> dependencies = (metadata == null) ? Collections.emptyList() : metadata.dependencies();
        nodeState.get(State.GREY).add(clazz);

        for (Class<?> dependency : dependencies) {
            dfs(dependency, sorted, nodeState, registry);
        }

        nodeState.get(State.GREY).remove(clazz);
        nodeState.get(State.BLACK).add(clazz);

        if (metadata != null) {
            sorted.add(metadata);
        }
    }
}