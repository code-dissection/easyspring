package com.github.codedissection.easyspring.topologysorter;

import com.github.codedissection.easyspring.topologysorter.enums.State;
import com.github.codedissection.easyspring.topologysorter.exception.CircularDependencyException;
import com.github.codedissection.easyspring.scanner.dto.Metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MetadataTopologySorter {

    public List<Metadata> getSortedMetadata(Set<Metadata> containers) {
        var sorted = new ArrayList<Metadata>();
        var nodeState = new HashMap<State, Set<Class<?>>>();
        nodeState.put(State.GREY, new HashSet<>());
        nodeState.put(State.BLACK, new HashSet<>());
        var registry = new HashMap<Class<?>, Metadata>();
        for (Metadata container : containers) {
            registry.put(container.getSourceClass(), container);
        }

        for (Metadata container : containers) {
            var clazz = container.getSourceClass();
            dfs(clazz, sorted, nodeState, registry);
        }

        return sorted;
    }

    private void dfs(Class<?> clazz, List<Metadata> sorted, Map<State, Set<Class<?>>> nodeState, Map<Class<?>, Metadata> registry) {

        if (clazz == null || clazz == Object.class) {
            return;
        }

        if (nodeState.get(State.BLACK).contains(clazz)) {
            return;
        }

        if (nodeState.get(State.GREY).contains(clazz)) {
            throw new CircularDependencyException("Circular dependency detected. Project structure is invalid...");
        }

        nodeState.get(State.GREY).add(clazz);

        var container = registry.get(clazz);
        List<Class<?>> dependencies = (container != null) ? container.getDependencies() : Collections.EMPTY_LIST;

        for (Class<?> dependency : dependencies) {
            dfs(dependency, sorted, nodeState, registry);
        }

        nodeState.get(State.GREY).remove(clazz);
        nodeState.get(State.BLACK).add(clazz);
        if (container != null) {
            sorted.add(container);
        }
    }
}
