package definition;

import definition.enums.BeanInstantiationStrategy;
import definition.enums.BeanReuseStrategy;
import definition.exception.BeanDefinitionCreationException;

import java.util.List;
import java.util.Objects;

public final class BeanDefinition {

    private final Class<?> sourceClass;

    private final List<Class<?>> dependencies;

    private final BeanReuseStrategy beanReuseStrategy;

    private final BeanInstantiationStrategy beanInstantiationStrategy;

    private BeanDefinition(Builder builder) {
        this.sourceClass = builder.sourceClass;
        this.dependencies = builder.dependencies;
        this.beanReuseStrategy = builder.beanReuseStrategy;
        this.beanInstantiationStrategy = builder.beanInstantiationStrategy;
    }

    public Class<?> getSourceClass() {
        return sourceClass;
    }

    public List<Class<?>> getDependencies() {
        return dependencies;
    }

    public BeanReuseStrategy getBeanReuseStrategy() {
        return beanReuseStrategy;
    }

    public BeanInstantiationStrategy getBeanInstantiationStrategy() {
        return beanInstantiationStrategy;
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
        return Objects.hash(sourceClass);
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "sourceClass=" + sourceClass +
                ", dependencies=" + dependencies +
                ", beanReuseStrategy=" + beanReuseStrategy +
                ", beanInstantiationStrategy=" + beanInstantiationStrategy +
                '}';
    }

    public static class Builder {
        private final Class<?> sourceClass;
        private final List<Class<?>> dependencies;
        private BeanReuseStrategy beanReuseStrategy = BeanReuseStrategy.SINGLETON;
        private BeanInstantiationStrategy beanInstantiationStrategy = BeanInstantiationStrategy.EAGER;

        public Builder(Class<?> sourceClass, List<Class<?>> dependencies) {
            if (sourceClass == null) {
                throw new BeanDefinitionCreationException("Can't create BeanDefinition: sourceClass can't be null...");
            }
            this.sourceClass = sourceClass;

            if (dependencies == null) {
                throw new BeanDefinitionCreationException("Can't create BeanDefinition: dependencies can't be null...");
            }
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
            return new BeanDefinition(this);
        }
    }

}
