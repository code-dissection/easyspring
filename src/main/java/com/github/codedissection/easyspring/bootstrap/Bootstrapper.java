package com.github.codedissection.easyspring.bootstrap;

import com.github.codedissection.easyspring.bean.BeanFactory;
import com.github.codedissection.easyspring.definition.DefinitionFactory;
import com.github.codedissection.easyspring.projectcontext.ProjectContext;
import com.github.codedissection.easyspring.projectscanner.ProjectStructureScanner;
import com.github.codedissection.easyspring.topologysorter.MetadataTopologySorter;

public final class Bootstrapper {

    public ProjectContext process(String packageToScan) {

        var projectScanner = new ProjectStructureScanner();
        var projectConfiguration = projectScanner.getProjectConfiguration(packageToScan);

        var topologySorter = new MetadataTopologySorter();
        var sortedMetadata = topologySorter.getSortedMetadata(projectConfiguration);

        var definitionFactory = new DefinitionFactory();
        var definitionMap = definitionFactory.createSortedBeanDefinitionMap(sortedMetadata);

        var beanFactory = new BeanFactory();
        var beans = beanFactory.createBeanMap(definitionMap);

        var projectContext = new ProjectContext();
        projectContext.saveDefinitions(definitionMap);
        projectContext.saveBeans(beans);

        return projectContext;
    }


}