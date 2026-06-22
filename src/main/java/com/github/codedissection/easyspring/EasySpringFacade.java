package com.github.codedissection.easyspring;

import com.github.codedissection.easyspring.bootstrap.Bootstrapper;

import java.util.Objects;

public class EasySpringFacade {

    private EasySpringFacade() {
        throw new UnsupportedOperationException("Framework bootstrap class can not be instantiated.");
    }

    public static void run(Class<?> mainClass) {
        Objects.requireNonNull(mainClass, "Main configuration class must not be null.");

        Bootstrapper bootstrapper = new Bootstrapper();
        String packageToScan = mainClass.getPackageName();
        bootstrapper.process(packageToScan);
    }
}
