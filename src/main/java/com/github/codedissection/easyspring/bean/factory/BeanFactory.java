package com.github.codedissection.easyspring.bean.factory;

import com.github.codedissection.easyspring.bean.exception.BeanCreateException;
import com.github.codedissection.easyspring.bean.storage.BeanStorage;
import com.github.codedissection.easyspring.definition.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanFactory {

    private final BeanStorage beanStorage;

    public BeanFactory(BeanStorage beanStorage) {
        this.beanStorage = beanStorage;
    }

    public Map<Class<?>, Object> createBeanRegistry(List<BeanDefinition> definitions) {
        var keyBeanPairs = new HashMap<Class<?>, Object>();
        for (BeanDefinition definition : definitions) {
            List<Class<?>> dependencies = definition.getDependencies();
            List<Object> beans = beanStorage.getBeans(dependencies); //конкретные реализации. на один класс может быть ровно 1 бин, не более.
            Object bean = createBean(definition, beans);
            keyBeanPairs.put(definition.getSourceClass(), bean);
        }
        return keyBeanPairs;
    }

    private <T> T createBean(BeanDefinition definition, List<Object> beansForImport) {
        Constructor<?>[] constructors = definition
                .getSourceClass()
                .getDeclaredConstructors();
        if (constructors.length > 1) {
            throw new BeanCreateException("Failed phase 2: more than 1 constructor in class " + definition.getSourceClass().getName());
        }
        Constructor<?> constructor = constructors[0];
        constructor.setAccessible(true);
        try {
            return (T) constructor.newInstance(beansForImport.toArray());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new BeanCreateException("Failed phase 3: can't instantiate object for class " + definition.getSourceClass().getName(), e);
        }
    }
}