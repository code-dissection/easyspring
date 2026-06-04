package bootstrap;

import bootstrap.dto.ClassPropertiesContainer;
import definition.BeanDefinition;

import java.util.List;
import java.util.Map;

public class ContextPipeline {

    private final PipelineWorker pipelineWorker = new PipelineWorker();
    //todo get value
    private String packageToScan;

    public void start(String packageToScan) {
        //Phase 1. Scan classpath for annotated classes
        List<ClassPropertiesContainer> classInfos = pipelineWorker.scanAppCode(packageToScan);

        //Phase 2. Create bean definitions
        Map<String, BeanDefinition> definitions = pipelineWorker.createDefinitions(classInfos);

    }
}