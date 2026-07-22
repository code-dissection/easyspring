package com.github.codedissection.easyspring.bootstrap;

import com.github.codedissection.easyspring.bean.BeanFactory;
import com.github.codedissection.easyspring.definition.BeanDefinitionFactory;
import com.github.codedissection.easyspring.context.ProjectContext;
import com.github.codedissection.easyspring.scanner.ProjectScanner;
import com.github.codedissection.easyspring.settingscanner.SettingsFileScanner;
import com.github.codedissection.easyspring.topologysorter.MetadataTopologySorter;

public final class Bootstrapper {

    public ProjectContext process(String packageToScan) {

        var settingsScanner = new SettingsFileScanner();
        var settings = settingsScanner.getSettings();

        var projectScanner = new ProjectScanner();
        var projectConfiguration = projectScanner.getProjectConfiguration(packageToScan);

        var topologySorter = new MetadataTopologySorter();
        var sortedMetadata = topologySorter.getSortedMetadata(projectConfiguration);

        var definitionFactory = new BeanDefinitionFactory();
        var definitionMap = definitionFactory.createSortedBeanDefinitionMap(sortedMetadata, settings);

        var beanFactory = new BeanFactory();
        var beans = beanFactory.createBeanMap(definitionMap);

        var projectContext = new ProjectContext();
        projectContext.saveDefinitions(definitionMap);
        projectContext.saveBeans(beans);

        return projectContext;
    }
}