package com.github.codedissection.easyspring.context;

import com.github.codedissection.easyspring.definition.beandefinition.BeanDefinition;
import com.github.codedissection.easyspring.context.storage.BeanStorage;
import com.github.codedissection.easyspring.context.storage.DefinitionStorage;

import java.util.Map;

public class ProjectContext {

    private final DefinitionStorage definitionStorage = new DefinitionStorage();
    private final BeanStorage beanStorage = new BeanStorage();

    public void saveDefinitions(Map<Class<?>, BeanDefinition> definitions) {
        definitionStorage.saveBeanDefinitions(definitions);
    }

    public void saveBeans(Map<Class<?>, Object> beans) {
        beanStorage.saveBeans(beans);
    }
}
