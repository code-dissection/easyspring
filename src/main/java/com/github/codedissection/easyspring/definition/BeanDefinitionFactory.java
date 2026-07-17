package com.github.codedissection.easyspring.definition;

import com.github.codedissection.easyspring.definition.annotation.ValueFrom;
import com.github.codedissection.easyspring.definition.enums.BeanReuseStrategy;
import com.github.codedissection.easyspring.definition.exception.AbsentConstructorException;
import com.github.codedissection.easyspring.definition.model.BeanDefinition;
import com.github.codedissection.easyspring.definition.exception.MissingImplementationException;
import com.github.codedissection.easyspring.definition.exception.MultipleImplementationException;
import com.github.codedissection.easyspring.definition.model.ClassSettings;
import com.github.codedissection.easyspring.scanner.annotation.OneOff;
import com.github.codedissection.easyspring.scanner.model.TypeMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.codedissection.easyspring.definition.exception.message.MessageTemplate.MISSING_CONSTRUCTOR_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.definition.exception.message.MessageTemplate.MULTIPLE_IMPLEMENTATIONS_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.definition.exception.message.MessageTemplate.RESOLVED_IMPLEMENTATION_ERROR_TEMPLATE;

public class BeanDefinitionFactory {

    public LinkedHashMap<Class<?>, BeanDefinition> createSortedBeanDefinitionMap(List<TypeMetadata> containers, Map<String, Object> settings) {
        var definitionMap = new LinkedHashMap<Class<?>, BeanDefinition>();
        var resolvedTypes = getResolvedTypes(containers);
        for (TypeMetadata container : containers) {
            var sourceClass = container.sourceClass();
            var dependencies = container.dependencies().stream()
                    .<Class<?>>map(rawType -> {
                        var resolvedType = resolvedTypes.get(rawType);
                        if (resolvedType == null)
                            throw new MissingImplementationException(String.format(
                                    RESOLVED_IMPLEMENTATION_ERROR_TEMPLATE,
                                    rawType
                            ));
                        return resolvedType;
                    })
                    .toList();
            var beanReuseStrategy = getBeanReuseStrategy(container);
            var classSettings = getClassSettings(sourceClass, settings);
            var beanDefinition = new BeanDefinition.Builder(sourceClass, dependencies)
                    .withBeanReuseStrategy(beanReuseStrategy)
                    .withBeanSettings(classSettings)
                    .build();
            definitionMap.put(sourceClass, beanDefinition);
        }
        return definitionMap;
    }

    private ClassSettings getClassSettings(Class<?> sourceClass, Map<String, Object> settings){
        Map<String, Object> localSettings = new HashMap<>();
        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field: fields){
            if (field.isAnnotationPresent(ValueFrom.class)) {
                var value = field.getAnnotation(ValueFrom.class).value();
                localSettings.put(
                        value,
                        settings.get(value)
                );
            }
        }
        var constructor = Arrays.stream(sourceClass.getDeclaredConstructors())
                .filter(any -> !any.isSynthetic())
                .findFirst()
                .orElseThrow(() ->
                        new AbsentConstructorException(String.format(
                                MISSING_CONSTRUCTOR_ERROR_TEMPLATE,
                                sourceClass.getName()
                        ))
                );
        Parameter[] parameters = constructor.getParameters();
        for (Parameter parameter: parameters){
            if (parameter.isAnnotationPresent(ValueFrom.class)) {
                var value = parameter.getAnnotation(ValueFrom.class).value();
                localSettings.put(
                        value,
                        settings.get(value)
                );
            }
        }

        return new ClassSettings(
                sourceClass,
                localSettings.isEmpty() ?  null : localSettings
        );
    }

    private BeanReuseStrategy getBeanReuseStrategy(TypeMetadata container) {
        if (container.annotations().contains(OneOff.class))
            return BeanReuseStrategy.ONEOFF;
        else
            return BeanReuseStrategy.SINGLETON;
    }

    private Map<Class<?>, Class<?>> getResolvedTypes(List<TypeMetadata> containers) {
        var flattenHierarchy = new LinkedHashMap<Class<?>, Class<?>>();
        for (TypeMetadata container : containers) {
            var child = container.sourceClass();
            var ancestors = getAllClassAncestors(child);
            flattenHierarchy.put(child, child);
            for (Class<?> ancestor : ancestors) {
                var alreadyExistChild = flattenHierarchy.get(ancestor);
                if (alreadyExistChild != null) {
                    throw new MultipleImplementationException(String.format(
                            MULTIPLE_IMPLEMENTATIONS_ERROR_TEMPLATE,
                            ancestor,
                            child,
                            alreadyExistChild
                    ));
                }
                flattenHierarchy.put(ancestor, child);
            }
        }
        return flattenHierarchy;
    }

    private Set<Class<?>> getAllClassAncestors(Class<?> clazz) {
        var ancestors = new HashSet<Class<?>>();
        dfs(clazz, ancestors);
        return ancestors;
    }

    private void dfs(Class<?> clazz, Set<Class<?>> ancestors) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        var directAncestors = new HashSet<Class<?>>();
        var superClass = clazz.getSuperclass();
        var superInterfaces = Arrays.asList(clazz.getInterfaces());

        if (!superInterfaces.isEmpty()) {
            directAncestors.addAll(superInterfaces);
        }
        if (superClass != null && superClass != Object.class) {
            directAncestors.add(superClass);
        }

        for (Class<?> ancestor : directAncestors) {
            if (ancestors.add(ancestor)) {
                dfs(ancestor, ancestors);
            }
        }
    }
}