package com.github.codedissection.easyspring.scanner.dto;

import com.github.codedissection.easyspring.definition.exception.BeanDefinitionCreateException;

import java.util.List;
import java.util.Objects;

public class Metadata {
    private final String name;
    private final Class<?> sourceClass;
    private final List<Class<?>> dependencies;

    Metadata(Builder builder) {
        this.name = builder.name;
        this.sourceClass = builder.sourceClass;
        this.dependencies = builder.dependencies;
    }

    public String getName() {
        return this.name;
    }

    public List<Class<?>> getDependencies() {
        return this.dependencies;
    }

    public Class<?> getSourceClass() {
        return this.sourceClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(name, metadata.name) && Objects.equals(sourceClass, metadata.sourceClass) && Objects.equals(dependencies, metadata.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sourceClass, dependencies);
    }

    public static class Builder {
        private String name;
        private Class<?> sourceClass;
        private List<Class<?>> dependencies;

        public Builder withName(String name) {
            if (name == null) {
                throw new BeanDefinitionCreateException("Can't create ClassMetadataContainer: name can't be null...");
            }
            this.name = name;
            return this;
        }

        public Builder withSourceClass(Class<?> sourceClass) {
            if (sourceClass == null) {
                throw new BeanDefinitionCreateException("Can't create ClassMetadataContainer: sourceClass can't be null...");
            }
            this.sourceClass = sourceClass;
            return this;
        }

        public Builder withDependencies(List<Class<?>> dependencies) {
            if (dependencies == null) {
                throw new BeanDefinitionCreateException("Can't create ClassMetadataContainer: dependencies can't be null...");
            }
            this.dependencies = List.copyOf(dependencies);
            return this;
        }

        public Metadata build() {
            if (name == null) {
                throw new BeanDefinitionCreateException("Can't create ClassMetadataContainer: name can't be null...");
            }

            if (sourceClass == null) {
                throw new BeanDefinitionCreateException("Can't create ClassMetadataContainer: sourceClass can't be null...");
            }

            if (dependencies == null) {
                this.dependencies = List.of();
            }
            return new Metadata(this);
        }
    }
}
