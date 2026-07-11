package com.github.codedissection.easyspring.definition.model;

import com.github.codedissection.easyspring.definition.enums.BeanInstantiationStrategy;
import com.github.codedissection.easyspring.definition.enums.BeanReuseStrategy;
import com.github.codedissection.easyspring.definition.exception.BeanDefinitionCreateException;

import java.util.List;
import java.util.Objects;

public record BeanDefinition(
        Class<?> sourceClass,
        List<Class<?>> dependencies,
        BeanReuseStrategy beanReuseStrategy,
        BeanInstantiationStrategy beanInstantiationStrategy
) {

    public BeanDefinition {
        Objects.requireNonNull(sourceClass, "sourceClass must not be null");
        Objects.requireNonNull(dependencies, "dependencies must not be null");
        Objects.requireNonNull(beanReuseStrategy, "beanReuseStrategy must not be null");
        Objects.requireNonNull(beanInstantiationStrategy, "beanInstantiationStrategy must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanDefinition that = (BeanDefinition) o;
        return Objects.equals(sourceClass, that.sourceClass);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sourceClass);
    }

    public static class Builder {
        private final Class<?> sourceClass;
        private final List<Class<?>> dependencies;
        private BeanReuseStrategy beanReuseStrategy = BeanReuseStrategy.SINGLETON;
        private BeanInstantiationStrategy beanInstantiationStrategy = BeanInstantiationStrategy.EAGER;

        public Builder(Class<?> sourceClass, List<Class<?>> dependencies) throws BeanDefinitionCreateException {
            this.sourceClass = sourceClass;
            this.dependencies = List.copyOf(dependencies);
        }

        public Builder withBeanReuseStrategy(BeanReuseStrategy beanReuseStrategy) {
            if (beanReuseStrategy != null) {
                this.beanReuseStrategy = beanReuseStrategy;
            }
            return this;
        }

        public Builder withBeanInstantiationStrategy(BeanInstantiationStrategy beanInstantiationStrategy) {
            if (beanInstantiationStrategy != null) {
                this.beanInstantiationStrategy = beanInstantiationStrategy;
            }
            return this;
        }

        public BeanDefinition build() {
            return new BeanDefinition(sourceClass, dependencies, beanReuseStrategy, beanInstantiationStrategy);
        }
    }
}