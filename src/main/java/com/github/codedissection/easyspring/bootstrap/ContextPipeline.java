package com.github.codedissection.easyspring.bootstrap;

import com.github.codedissection.easyspring.bean.resolver.BeanDefinitionSorter;
import com.github.codedissection.easyspring.bootstrap.dto.MetadataContainer;
import com.github.codedissection.easyspring.definition.BeanDefinition;

import java.util.List;
import java.util.Map;

public class ContextPipeline {

    private final PipelineWorker pipelineWorker = new PipelineWorker();
    private final BeanDefinitionSorter beanDefinitionSorter = new BeanDefinitionSorter();

    public void process(String packageToScan) {
        //Phase 1. Scan classpath for annotated classes
        List<MetadataContainer> classInfos = pipelineWorker.getMetadataConfiguration(packageToScan);

        //Phase 2. Create bean definitions
        Map<String, BeanDefinition> definitions = pipelineWorker.createDefinitions(classInfos);

        //Phase 3. Topological sort
        List<BeanDefinition> sortedDefinitions = beanDefinitionSorter.sortBeanDefinitions(definitions);

    }
}