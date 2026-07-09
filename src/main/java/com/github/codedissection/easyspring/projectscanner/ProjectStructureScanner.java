package com.github.codedissection.easyspring.projectscanner;

import com.github.codedissection.easyspring.projectscanner.annotation.root.EasySpringAnnotation;
import com.github.codedissection.easyspring.definition.exception.BeanDefinitionCreateException;
import com.github.codedissection.easyspring.projectscanner.dto.Metadata;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ProjectStructureScanner {

    public Set<Metadata> getProjectConfiguration(String packageToScan) {
        Set<Metadata> classMetadataStorage = new HashSet<>();
        ClassGraph scanner = new ClassGraph()
                .acceptPackages(packageToScan)
                .enableClassInfo()
                .enableAnnotationInfo();

        try (ScanResult result = scanner.scan()) {
            var classes = result.getClassesWithAnnotation(EasySpringAnnotation.class.getName())
                    .stream()
                    .filter(classInfo -> classInfo.getName().startsWith(packageToScan))
                    .toList();
            for (ClassInfo info : classes) {
                var sourceClass = validateClass(info.loadClass());
                var className = info.getName();
                var constructor = getTheOnlyConstructor(sourceClass);
                var dependencies = getBeanDependencies(constructor);
                var container = new Metadata.Builder()
                        .withName(className)
                        .withSourceClass(sourceClass)
                        .withDependencies(dependencies)
                        .build();
                classMetadataStorage.add(container);
            }
            return classMetadataStorage;
        } catch (BeanDefinitionCreateException e) {
            throw e;
        } catch (Exception e) {
            //TODO Change exception type
            throw new BeanDefinitionCreateException("Pipeline phase 1 failed: ClassGraph crashed while scanning package " + packageToScan);
        }
    }

    private Class<?> validateClass(Class<?> clazz) {
        if (clazz.isEnum() ||
                clazz.isInterface() ||
                clazz.isAnnotation() ||
                Modifier.isAbstract(clazz.getModifiers())) {
            //TODO Change exception type
            throw new BeanDefinitionCreateException("Pipeline phase 1 failed: invariant violated. Invalid annotated type " + clazz.getName());
        }
        if (clazz.getDeclaredConstructors().length == 0) {
            //TODO Change exception type
            throw new BeanDefinitionCreateException("Pipeline phase 1 failed: constructor is absent in class " + clazz.getName());
        }
        return clazz;
    }

    private Constructor<?> getTheOnlyConstructor(Class<?> sourceClass) {
        return Stream.of(sourceClass.getDeclaredConstructors())
                .reduce((first, second) -> {
                    //TODO Change exception type
                    throw new BeanDefinitionCreateException("Pipeline phase 1 failed: there is more than 1 constructor in class: " + sourceClass.getName());
                })
                //TODO Change exception type
                .orElseThrow(() -> new BeanDefinitionCreateException("Pipeline phase 1 failed: there is no constructor in class: " + sourceClass.getName()));
    }

    private List<Class<?>> getBeanDependencies(Constructor<?> constructor) {
        //TODO Add validation by result
        return Arrays.asList(constructor.getParameterTypes());
    }

}
