package bootstrap.dto;

import definition.exception.BeanDefinitionCreationException;

import java.util.List;

public class ClassPropertiesContainer {
    private final String name;
    private final Class<?> sourceClass;
    private final List<Class<?>> dependencies;

    ClassPropertiesContainer(Builder builder) {
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
                throw new BeanDefinitionCreationException("Can't create ClassPropertiesContainer: name can't be null...");
            }
            this.name = name;
            return this;
        }

        public Builder withSourceClass(Class<?> sourceClass) {
            if (sourceClass == null) {
                throw new BeanDefinitionCreationException("Can't create ClassPropertiesContainer: sourceClass can't be null...");
            }
            this.sourceClass = sourceClass;
            return this;
        }

        public Builder withDependencies(List<Class<?>> dependencies) {
            if (dependencies == null) {
                throw new BeanDefinitionCreationException("Can't create ClassPropertiesContainer: dependencies can't be null...");
            }
            this.dependencies = List.copyOf(dependencies);
            return this;
        }

        public ClassPropertiesContainer build() {
            if (name == null) {
                throw new BeanDefinitionCreationException("Can't create ClassPropertiesContainer: name can't be null...");
            }

            if (sourceClass == null) {
                throw new BeanDefinitionCreationException("Can't create ClassPropertiesContainer: sourceClass can't be null...");
            }

            if (dependencies == null) {
                this.dependencies = List.of();
            }
            return new ClassPropertiesContainer(this);
        }
    }
}
