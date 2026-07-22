package com.github.codedissection.easyspring.context;

import com.github.codedissection.easyspring.bean.BeanFactory;
import com.github.codedissection.easyspring.context.annotation.Close;
import com.github.codedissection.easyspring.context.exception.ShutdownProjectException;
import com.github.codedissection.easyspring.context.storage.BeanStorage;
import com.github.codedissection.easyspring.context.storage.DefinitionStorage;
import com.github.codedissection.easyspring.definition.model.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.github.codedissection.easyspring.context.exception.message.MessageTemplate.CLOSE_METHOD_ACCESS_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.context.exception.message.MessageTemplate.INVOCATION_METHOD_ACCESS_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.context.exception.message.MessageTemplate.MULTIPLE_CLOSE_METHODS_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.context.exception.message.MessageTemplate.PROJECT_SHUTDOWN_TEMPLATE;
import static com.github.codedissection.easyspring.context.exception.message.MessageTemplate.STATIC_CLOSE_METHOD_ERROR_TEMPLATE;

public class ProjectContext {

    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final BeanStorage beanStorage = new BeanStorage();
    private final BeanFactory beanFactory = new BeanFactory();
    private final DefinitionStorage definitionStorage = new DefinitionStorage();

    public ProjectContext() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public void saveDefinitions(LinkedHashMap<Class<?>, BeanDefinition> definitions) {
        definitionStorage.saveBeanDefinitions(definitions);
    }

    public void saveBeans(Map<Class<?>, Object> beans) {
        beanStorage.saveBeans(beans);
    }

    public <T> T getBean(Class<T> clazz) {
        if(isClosed.get())
            throw new ShutdownProjectException(String.format(
                    PROJECT_SHUTDOWN_TEMPLATE,
                    clazz.getName()
            ));
        var bean = beanStorage.getBean(clazz);
        if (bean != null)
            return (T) bean;

        var beansForImport = new ArrayList<>();
        var definition = definitionStorage.getBeanDefinition(clazz);
        var dependencies = definition.dependencies();
        for (Class<?> dependency : dependencies) {
            var createdDependency = getBean(dependency);
            beansForImport.add(createdDependency);
        }

        bean = beanFactory.createBean(definition, beansForImport);
        return (T) bean;
    }

    private void close() {
        if (!isClosed.compareAndSet(false, true))
            return;
        var toDieOrder = new ArrayList<>(definitionStorage.getBeanDefinitions().keySet());
        Collections.reverse(toDieOrder);

        for (Class<?> key : toDieOrder) {
            if (!beanStorage.contains(key))
                continue;
            var bean = beanStorage.getBean(key);
            var closeMethods = Arrays.stream(bean.getClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Close.class))
                    .toList();
            if (closeMethods.isEmpty())
                continue;
            if (closeMethods.size() > 1) {
                var pretty = closeMethods.stream()
                        .map(Method::getName)
                        .collect(Collectors.joining(", "));
                throw new ShutdownProjectException(String.format(
                        MULTIPLE_CLOSE_METHODS_ERROR_TEMPLATE,
                        key.getName(),
                        pretty
                ));
            }
            var method = closeMethods.getFirst();
            if (Modifier.isStatic(method.getModifiers()))
                throw new ShutdownProjectException(String.format(
                        STATIC_CLOSE_METHOD_ERROR_TEMPLATE,
                        key.getName(),
                        method.getName()
                ));
            try {
                method.setAccessible(true);
                method.invoke(bean);
            } catch (IllegalAccessException e) {
                System.err.println(String.format(
                        CLOSE_METHOD_ACCESS_ERROR_TEMPLATE,
                        key.getName(),
                        method.getName()
                ));
            } catch (InvocationTargetException e) {
                var realCause = e.getCause();
                System.err.println(String.format(
                        INVOCATION_METHOD_ACCESS_ERROR_TEMPLATE,
                        key.getName(),
                        method.getName(),
                        realCause
                ));
                if (realCause != null)
                    realCause.printStackTrace();
            }
        }

        toDieOrder.clear();
        definitionStorage.clear();
        beanStorage.clear();
    }
}
