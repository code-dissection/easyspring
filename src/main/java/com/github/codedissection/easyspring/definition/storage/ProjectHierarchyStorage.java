//package com.github.codedissection.easyspring.definition.storage;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class ProjectHierarchyStorage {
//    private volatile Map<Class<?>, List<Class<?>>> projectHierarchy;
//
//    public Map<Class<?>, List<Class<?>>> getProjectHierarchy() {
//        return projectHierarchy;
//    }
//
//    public void saveProjectHierarchy(Map<Class<?>, List<Class<?>>> projectHierarchy) {
//        Map<Class<?>, List<Class<?>>> immutableSnapshot = projectHierarchy.entrySet().stream()
//                .collect(Collectors.toUnmodifiableMap(
//                        Map.Entry::getKey,
//                        es -> List.copyOf(es.getValue())
//                ));
//        this.projectHierarchy = immutableSnapshot;
//    }
//}
