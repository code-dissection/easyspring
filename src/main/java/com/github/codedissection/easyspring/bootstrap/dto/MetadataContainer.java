package com.github.codedissection.easyspring.bootstrap.dto;

import com.github.codedissection.easyspring.definition.exception.BeanDefinitionCreateException;

import java.util.List;

public class MetadataContainer {
    private final String name;
    private final Class<?> sourceClass;
    private final List<Class<?>> dependencies;

    MetadataContainer(Builder builder) {
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

        public MetadataContainer build() {
            if (name == null) {
                throw new BeanDefinitionCreateException("Can't create ClassMetadataContainer: name can't be null...");
            }

            if (sourceClass == null) {
                throw new BeanDefinitionCreateException("Can't create ClassMetadataContainer: sourceClass can't be null...");
            }

            if (dependencies == null) {
                this.dependencies = List.of();
            }
            return new MetadataContainer(this);
        }
    }
}
