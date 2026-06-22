package com.github.codedissection.easyspring.bean.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanStorage {

    private final ConcurrentHashMap<Class<?>, List<Object>> beanStorage = new ConcurrentHashMap<>();

    public List<Object> getBeans(List<Class<?>> types) {
        var beans = new ArrayList<>();
        for (Class<?> type: types) {
            beans.add(getBean(type));
        }
        return beans;
    }

    public Object getBean(Class<?> type) {
        var beans = beanStorage.get(type);
        if (beans.size() > 1) {
            //TODO Написать кастомный эксепшн
            throw new RuntimeException("Механизм выбора приоритетного бина еще не реализован. Более одного экземпляра на 1 тип.");
        }
        if (beans.isEmpty()) {
            throw new RuntimeException("Не правильно задан тип бина. Такого бина не существует");
        }
        return beans.getFirst();
    }

    public void saveBeanRegistry(Map<Class<?>, Object> keyBeanPairs) {
        for (Map.Entry<Class<?>, Object> entry : keyBeanPairs.entrySet()) {
            saveBean(entry.getKey(), entry.getValue());
        }
    }

    public void saveBean(Class<?> sourceClass, Object bean) {
        List<Class<?>> superTypes = getAllSuperTypes(sourceClass);
        for (Class<?> clazz : superTypes) {
            if (!beanStorage.containsKey(clazz)) {
                var beans = new ArrayList<>();
                beans.add(bean);
                beanStorage.put(clazz, beans);
            } else {
                var list = beanStorage.get(clazz);
                list.add(bean);
            }
        }
    }

    private List<Class<?>> getAllSuperTypes(Class<?> type) {
        var supers = new ArrayList<Class<?>>();
        if (type == null || type == Object.class) {
            return supers;
        }

        var superClass = type.getSuperclass();
        var interfaces = Arrays.asList(type.getInterfaces());
        if (superClass != Object.class)
            supers.add(superClass);
        if (!interfaces.isEmpty())
            supers.addAll(interfaces);

        for (Class<?> clazz : supers) {
            getAllSuperTypes(clazz);
        }
        supers.add(type);
        return supers;
    }

}
