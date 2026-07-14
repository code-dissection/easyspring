package com.github.codedissection.easyspring.scanner.model;

import java.util.List;

public record TypeMetadata(
        Class<?> sourceClass,
        List<Class<?>> dependencies,
        List<Class<?>> annotations

) {
    public TypeMetadata {
        dependencies = List.copyOf(dependencies);
        annotations = List.copyOf(annotations);
    }
}