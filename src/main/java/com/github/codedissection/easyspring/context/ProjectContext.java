package com.github.codedissection.easyspring.context;

import com.github.codedissection.easyspring.bean.BeanFactory;
import com.github.codedissection.easyspring.context.storage.BeanStorage;
import com.github.codedissection.easyspring.context.storage.DefinitionStorage;
import com.github.codedissection.easyspring.definition.model.BeanDefinition;

import java.util.ArrayList;
import java.util.Map;

public class ProjectContext {

    private final BeanStorage beanStorage = new BeanStorage();
    private final BeanFactory beanFactory = new BeanFactory();
    private final DefinitionStorage definitionStorage = new DefinitionStorage();

    public void saveDefinitions(Map<Class<?>, BeanDefinition> definitions) {
        definitionStorage.saveBeanDefinitions(definitions);
    }

    public void saveBeans(Map<Class<?>, Object> beans) {
        beanStorage.saveBeans(beans);
    }

    public <T> T getBean(Class<T> clazz) {
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
}
