package com.github.codedissection.easyspring.bootstrap;

import com.github.codedissection.easyspring.bean.factory.BeanFactory;
import com.github.codedissection.easyspring.bean.storage.BeanStorage;
import com.github.codedissection.easyspring.definition.BeanDefinition;
import com.github.codedissection.easyspring.definition.dto.TypeMetadataContainer;
import com.github.codedissection.easyspring.definition.factory.DefinitionFactory;
import com.github.codedissection.easyspring.definition.scaner.ProjectStructureScanner;
import com.github.codedissection.easyspring.definition.storage.DefinitionStorage;

import java.util.List;
import java.util.Map;

public class ContextPipeline {

    private final ProjectStructureScanner projectStructureScanner = new ProjectStructureScanner();
    private final DefinitionFactory definitionFactory = new DefinitionFactory();
    private final DefinitionStorage definitionStorage = new DefinitionStorage();
    private final BeanStorage beanStorage = new BeanStorage();
    private final BeanFactory beanFactory = new BeanFactory(beanStorage);

    public void process(String packageToScan) {
        List<TypeMetadataContainer> classInfos = projectStructureScanner.getProjectMetadataConfiguration(packageToScan);

        Map<Class<?>, BeanDefinition> definitions = definitionFactory.createBeanDefinitions(classInfos);
        List<BeanDefinition> sortedDefinitions = definitionFactory.sortBeanDefinitions(definitions);
        definitionStorage.saveBeanDefinitions(definitions, sortedDefinitions);

        var beanRegistry = beanFactory.createBeanRegistry(sortedDefinitions);
        beanStorage.saveBeanRegistry(beanRegistry);
    }
}