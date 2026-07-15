package com.github.codedissection.easyspring.bean;

import com.github.codedissection.easyspring.bean.exception.BeanCreateException;
import com.github.codedissection.easyspring.bean.exception.message.MessageTemplate;
import com.github.codedissection.easyspring.definition.enums.BeanReuseStrategy;
import com.github.codedissection.easyspring.definition.model.BeanDefinition;
import com.github.codedissection.easyspring.scanner.annotation.Init;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.codedissection.easyspring.bean.exception.message.MessageTemplate.ILLEGAL_ACCESS_EXCEPTION_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.bean.exception.message.MessageTemplate.INIT_METHOD_HAS_PARAMETERS_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.bean.exception.message.MessageTemplate.INVOCATION_TARGET_EXCEPTION_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.bean.exception.message.MessageTemplate.MULTIPLE_INIT_ANNOTATED_METHODS_ERROR_TEMPLATE;

public class BeanFactory {

    public Map<Class<?>, Object> createBeanMap(LinkedHashMap<Class<?>, BeanDefinition> definitionMap) {
        var beanStorage = new BeanStorageMap();
        for (Map.Entry<Class<?>, BeanDefinition> definitionPair : definitionMap.entrySet()) {
            var definition = definitionPair.getValue();
            if (definition.beanReuseStrategy() == BeanReuseStrategy.ONEOFF)
                continue;
            List<Class<?>> dependencies = definition.dependencies();
            List<Object> beans = beanStorage.getResolvedDependencyToObjectList(dependencies);
            var bean = createBean(definition, beans);
            beanStorage.saveBean(definition.sourceClass(), bean);
        }
        return Collections.unmodifiableMap(beanStorage.getBeanMap());
    }

    public <T> T createBean(BeanDefinition definition, List<Object> beansForImport) {
        var constructor = getConstructor(definition);
        constructor.setAccessible(true);
        try {
            var bean = constructor.newInstance(beansForImport.toArray());
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

    private void invokeInitAnnotatedMethod(Object bean) {
        var methods = Arrays.stream(bean.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Init.class))
                .toList();
        if (methods.isEmpty())
            return;
        if (methods.size() > 1)
            throw new BeanCreateException(String.format(
                    MULTIPLE_INIT_ANNOTATED_METHODS_ERROR_TEMPLATE,
                    bean.getClass().getName(),
                    methods
            ));
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
            var realCause = e.getCause();
            throw new BeanCreateException(String.format(
                    INVOCATION_TARGET_EXCEPTION_ERROR_TEMPLATE,
                    realCause
            ), realCause);
        }
    }

    private Constructor<?> getConstructor(BeanDefinition definition) {
        Constructor<?>[] constructors = definition
                .sourceClass()
                .getDeclaredConstructors();
        if (constructors.length > 1) {
            throw new BeanCreateException(String.format(
                    MessageTemplate.MULTIPLE_CONSTRUCTORS_ERROR_TEMPLATE,
                    definition.sourceClass().getName(),
                    constructors.length
            ));
        }
        return constructors[0];
    }

    static class BeanStorageMap {
        Map<Class<?>, Object> beanStorage = new HashMap<>();

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