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
            var metadataList = new ArrayList<TypeMetadata>();
            for (Class<?> key : nodeState.get(State.GREY)) {
                metadataList.add(registry.get(key));
            }

            Class<?>[] cycleChain = new Class[metadataList.size() + 1];
            for (int i = 0; i <= metadataList.size(); i++) {
                for (TypeMetadata metadata : metadataList) {
                    if (metadata.dependencies().contains(clazz)) {
                        cycleChain[metadataList.size() - i] = metadata.sourceClass();
                        clazz = metadata.sourceClass();
                        break;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cycleChain.length - 1; i++) {
                var element = cycleChain[i].getName();
                sb.append(element + " ──> ");
            }
            sb.append(cycleChain[cycleChain.length-1].getName());

            throw new CircularDependencyException(String.format(
                    CIRCULAR_DEPENDENCY_ERROR_TEMPLATE,
                    sb
            ));
        }
        nodeState.get(State.GREY).add(clazz);

        var metadata = registry.get(clazz);
        List<Class<?>> dependencies = (metadata == null) ? Collections.emptyList() : metadata.dependencies();

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