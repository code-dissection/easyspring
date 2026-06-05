package com.github.codedissection.easyspring;

import com.github.codedissection.easyspring.bootstrap.ContextPipeline;

public class EasySpringFacade {

    private EasySpringFacade() {
    }

    public static void run(Class<?> mainClass) {
        ContextPipeline contextPipeline = new ContextPipeline();

        String packageToScan = mainClass.getPackageName();

        contextPipeline.process(packageToScan);
    }
}
