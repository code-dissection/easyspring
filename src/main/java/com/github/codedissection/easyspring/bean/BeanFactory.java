package com.github.codedissection.easyspring.bean;

import com.github.codedissection.easyspring.bean.annotation.Init;
import com.github.codedissection.easyspring.bean.exception.BeanCreateException;
import com.github.codedissection.easyspring.bean.exception.message.MessageTemplate;
import com.github.codedissection.easyspring.context.ProjectContext;
import com.github.codedissection.easyspring.definition.annotation.ValueFrom;
import com.github.codedissection.easyspring.definition.enums.BeanReuseStrategy;
import com.github.codedissection.easyspring.definition.model.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.codedissection.easyspring.bean.exception.message.MessageTemplate.ILLEGAL_ACCESS_EXCEPTION_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.bean.exception.message.MessageTemplate.INCOMPATIBILITY_TYPES_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.bean.exception.message.MessageTemplate.INIT_METHOD_HAS_PARAMETERS_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.bean.exception.message.MessageTemplate.INVOCATION_TARGET_EXCEPTION_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.bean.exception.message.MessageTemplate.MULTIPLE_INIT_ANNOTATED_METHODS_ERROR_TEMPLATE;

public class BeanFactory {

    public Map<Class<?>, Object> createBeanMap(LinkedHashMap<Class<?>, BeanDefinition> definitionMap, ProjectContext projectContext) {
        var beanStorage = new BeanStorageMap();
        beanStorage.saveContext(projectContext);
        for (Map.Entry<Class<?>, BeanDefinition> definitionPair : definitionMap.entrySet()) {
            var definition = definitionPair.getValue();
            if (definition.beanReuseStrategy() == BeanReuseStrategy.ONEOFF)
                continue;
            List<Class<?>> dependencies = definition.dependencies();
            List<Object> beansForImport = beanStorage.getResolvedDependencyToObjectList(dependencies);
            var bean = createBean(definition, beansForImport);
            beanStorage.saveBean(definition.sourceClass(), bean);
        }
        return Collections.unmodifiableMap(beanStorage.getBeanMap());
    }

    public <T> T createBean(BeanDefinition definition, List<Object> beansForImport) {
        var constructor = getConstructor(definition);
        constructor.setAccessible(true);
        try {
            var parameters = constructor.getParameters();
            var toInject = new ArrayList<>();
            var settings = definition.classSettings().settings();
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(ValueFrom.class)) {
                    var key = parameter.getAnnotation(ValueFrom.class).value();
                    var value = settings.get(key);
                    var wrappedValue = convertToWrapperType(parameter.getType(), value);
                    toInject.add(wrappedValue);
                } else {
                    var type = parameter.getType();
                    var value = beansForImport.stream()
                            .filter(obj -> type.isAssignableFrom(obj.getClass()))
                            .findFirst()
                            .orElseThrow(() -> new BeanCreateException(String.format(
                                    INCOMPATIBILITY_TYPES_ERROR_TEMPLATE,
                                    definition.sourceClass(),
                                    parameter.getType()
                            )));
                    toInject.add(value);
                }
            }
            var bean = constructor.newInstance(toInject.toArray());

            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ValueFrom.class)) {
                    field.setAccessible(true);
                    var key = field.getAnnotation(ValueFrom.class).value();
                    var wrapped = convertToWrapperType(field.getType(), settings.get(key));
                    field.set(bean, wrapped);
                }
            }

            invokeInitAnnotatedMethod(bean);
            return (T) bean;
        } catch (InvocationTargetException | InstantiationException | IllegalArgumentException |
                 IllegalAccessException e) {
            String rootCause = "Unknown reason";
            if (e instanceof InvocationTargetException)
                rootCause = "Smth wrong with your code in constructor";
            if (e instanceof InstantiationException)
                rootCause = "It seems you try to instantiate interface or abstract class";
            if (e instanceof IllegalArgumentException)
                rootCause = "It seems arguments order is damaged";
            throw new BeanCreateException(String.format(
                    MessageTemplate.INSTANTIATION_ERROR_TEMPLATE,
                    definition.sourceClass().getName(),
                    Arrays.stream(constructor.getParameters())
                            .map(Parameter::getName)
                            .toList(),
                    beansForImport.stream()
                            .map(it -> (it == null) ? "null" : it.getClass().getName())
                            .toList(),
                    rootCause), e);
        }
    }

    private Object convertToWrapperType(Class<?> targetType, Object rawValue) {
        if (rawValue == null)
            return null;
        var strValue = String.valueOf(rawValue);
        if (targetType == int.class || targetType == Integer.class)
            return Integer.parseInt(strValue);
        if (targetType == long.class || targetType == Long.class)
            return Long.parseLong(strValue);
        if (targetType == boolean.class || targetType == Boolean.class)
            return Boolean.parseBoolean(strValue);
        if(targetType == String.class)
            return strValue;
        return rawValue;
    }

    private void invokeInitAnnotatedMethod(Object bean) {
        var methods = Arrays.stream(bean.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Init.class))
                .toList();
        if (methods.isEmpty())
            return;
        if (methods.size() > 1) {
            var pretty = methods.stream()
                    .map(Method::getName)
                    .collect(Collectors.joining(", "));
            throw new BeanCreateException(String.format(
                    MULTIPLE_INIT_ANNOTATED_METHODS_ERROR_TEMPLATE,
                    bean.getClass().getName(),
                    pretty
            ));
        }
        var method = methods.getFirst();
        if (method.getParameters().length > 0)
            throw new BeanCreateException(String.format(
                    INIT_METHOD_HAS_PARAMETERS_ERROR_TEMPLATE,
                    bean.getClass().getName(),
                    method.getName()
            ));
        try {
            method.setAccessible(true);
            method.invoke(bean);
        } catch (IllegalAccessException e) {
            throw new BeanCreateException(String.format(
                    ILLEGAL_ACCESS_EXCEPTION_ERROR_TEMPLATE,
                    method.getName()
            ));
        } catch (InvocationTargetException e) {
            var realCause = e.getMessage();
            throw new BeanCreateException(String.format(
                    INVOCATION_TARGET_EXCEPTION_ERROR_TEMPLATE,
                    realCause
            ), e);
        }
    }

    private Constructor<?> getConstructor(BeanDefinition definition) {
        var constructors = Arrays.stream(definition
                .sourceClass()
                .getDeclaredConstructors())
                .filter(c-> !c.isSynthetic())
                .toList();
        if (constructors.size() > 1) {
            throw new BeanCreateException(String.format(
                    MessageTemplate.MULTIPLE_CONSTRUCTORS_ERROR_TEMPLATE,
                    definition.sourceClass().getName(),
                    constructors.size()
            ));
        }
        return constructors.getFirst();
    }

    static class BeanStorageMap {
        Map<Class<?>, Object> beanStorage = new HashMap<>();

        private void saveContext(ProjectContext projectContext) {
            beanStorage.put(ProjectContext.class, projectContext);
        }

        private void saveBean(Class<?> key, Object bean) {
            beanStorage.put(key, bean);
        }

        private Object getBeanByType(Class<?> type) {
            return beanStorage.get(type);
        }

        private List<Object> getResolvedDependencyToObjectList(List<Class<?>> dependencies) {
            var list = new ArrayList<>();
            for (Class<?> dependency : dependencies) {
                list.add(getBeanByType(dependency));
            }
            return list;
        }

        private Map<Class<?>, Object> getBeanMap() {
            return beanStorage;
        }
    }
}