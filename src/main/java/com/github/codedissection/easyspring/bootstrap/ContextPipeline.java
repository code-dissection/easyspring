package com.github.codedissection.easyspring.bootstrap;

import com.github.codedissection.easyspring.bootstrap.dto.ClassPropertiesContainer;
import com.github.codedissection.easyspring.definition.BeanDefinition;

import java.util.List;
import java.util.Map;

public class ContextPipeline {

    private final PipelineWorker pipelineWorker = new PipelineWorker();

    public void process(String packageToScan) {
        //Phase 1. Scan classpath for annotated classes
        List<ClassPropertiesContainer> classInfos = pipelineWorker.scanAppCode(packageToScan);

        //Phase 2. Create bean definitions
        Map<String, BeanDefinition> definitions = pipelineWorker.createDefinitions(classInfos);

    }
}