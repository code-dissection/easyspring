package com.github.codedissection.easyspring.definition.model;

import java.util.Map;
import java.util.Objects;

public record ClassSettings(
        Class<?> sourceClass,
        Map<String, Object> settings
) {
    public ClassSettings {
        Objects.requireNonNull(sourceClass);
        settings = Map.copyOf(
                Objects.requireNonNullElse(settings, Map.of())
        );
    }
}
