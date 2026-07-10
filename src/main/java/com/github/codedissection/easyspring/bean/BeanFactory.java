package com.github.codedissection.easyspring.bean;

import com.github.codedissection.easyspring.bean.exception.BeanCreateException;
import com.github.codedissection.easyspring.bean.exception.message.MessageTemplate;
import com.github.codedissection.easyspring.definition.beandefinition.BeanDefinition;

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

public class BeanFactory {

    public Map<Class<?>, Object> createBeanMap(LinkedHashMap<Class<?>, BeanDefinition> definitionMap) {
        var beanStorage = new BeanStorage();
        for (Map.Entry<Class<?>, BeanDefinition> definitionPair : definitionMap.entrySet()) {
            var definition = definitionPair.getValue();
            List<Class<?>> dependencies = definition.getDependencies();
            List<Object> beans = beanStorage.getResolvedDependencyToObjectList(dependencies);
            var bean = createBean(definition, beans);
            beanStorage.saveBean(definition.getSourceClass(), bean);
        }
        return Collections.unmodifiableMap(beanStorage.getBeanMap());
    }

    private <T> T createBean(BeanDefinition definition, List<Object> beansForImport) {
        Constructor<?>[] constructors = definition
                .getSourceClass()
                .getDeclaredConstructors();
        if (constructors.length > 1) {
            throw new BeanCreateException(String.format(
                    MessageTemplate.MULTIPLE_CONSTRUCTORS_ERROR_TEMPLATE,
                    definition.getSourceClass().getName(),
                    constructors.length
            ));
        }
        Constructor<?> constructor = constructors[0];
        constructor.setAccessible(true);
        try {
            var bean = constructor.newInstance(beansForImport.toArray());
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
                    MessageTemplate.REFLECTION_INSTANTIATION_ERROR_TEMPLATE,
                    definition.getSourceClass().getName(),
                    Arrays.stream(constructor.getParameters())
                            .map(Parameter::getName)
                            .toList(),
                    beansForImport.stream()
                            .map(it -> (it == null) ? "null" : it.getClass().getName())
                            .toList(),
                    rootCause), e);
        }
    }

    static class BeanStorage {
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