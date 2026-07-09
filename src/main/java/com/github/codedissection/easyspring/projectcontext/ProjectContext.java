package com.github.codedissection.easyspring.projectcontext;

import com.github.codedissection.easyspring.definition.BeanDefinition;
import com.github.codedissection.easyspring.projectcontext.storage.BeanStorage;
import com.github.codedissection.easyspring.projectcontext.storage.DefinitionStorage;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectContext {

    private final DefinitionStorage definitionStorage = new DefinitionStorage();
    private final BeanStorage beanStorage = new BeanStorage();

    public void saveDefinitions(Map<Class<?>, BeanDefinition> definitions) {
        definitionStorage.saveBeanDefinitions(definitions);
    }

    public void saveBeans(LinkedHashMap<Class<?>, Object> beans) {
        beanStorage.saveBeans(beans);
    }
}
