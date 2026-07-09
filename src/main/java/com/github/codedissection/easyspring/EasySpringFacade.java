package com.github.codedissection.easyspring;

import com.github.codedissection.easyspring.bootstrap.Bootstrapper;
import com.github.codedissection.easyspring.projectcontext.ProjectContext;

import java.util.Objects;

public class EasySpringFacade {

    private EasySpringFacade() {
        throw new UnsupportedOperationException("Framework bootstrap class can not be instantiated...");
    }

    public static ProjectContext run(Class<?> mainClass) {
        Objects.requireNonNull(mainClass, "Main configuration class must not be null...");

        String packageToScan = mainClass.getPackageName();
        var bootstrapper = new Bootstrapper();

        return bootstrapper.process(packageToScan);
    }
}
