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

public final class Bootstrapper {

    private final DefinitionStorage definitionStorage = new DefinitionStorage();
    private final BeanStorage beanStorage = new BeanStorage();
    private final BeanFactory beanFactory = new BeanFactory(beanStorage);

    public void process(String packageToScan) {
        fillDefinitionStorage(definitionStorage, packageToScan);

        var beanIndex = beanFactory.getBeanIndex(definitionStorage.getSortedDefinitions());
        beanStorage.saveBeanRegistry(beanIndex);
    }

    private void fillDefinitionStorage(DefinitionStorage definitionStorage, String packageToScan) {
        var projectStructureScanner = new ProjectStructureScanner();
        var definitionFactory = new DefinitionFactory();

        List<TypeMetadataContainer> classInfos = projectStructureScanner.getProjectMetadataConfiguration(packageToScan);

        Map<Class<?>, BeanDefinition> definitions = definitionFactory.createBeanDefinitions(classInfos);
        List<BeanDefinition> sortedDefinitions = definitionFactory.sortBeanDefinitions(definitions);
        definitionStorage.saveBeanDefinitions(definitions, sortedDefinitions);
    }
}