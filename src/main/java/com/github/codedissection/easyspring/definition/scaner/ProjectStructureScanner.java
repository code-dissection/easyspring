package com.github.codedissection.easyspring.definition.scaner;

import com.github.codedissection.easyspring.definition.annotation.root.EasySpringAnnotation;
import com.github.codedissection.easyspring.definition.dto.TypeMetadataContainer;
import com.github.codedissection.easyspring.definition.exception.BeanDefinitionCreateException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ProjectStructureScanner {

    public List<TypeMetadataContainer> getProjectMetadataConfiguration(String packageToScan) {
        List<TypeMetadataContainer> classMetadataStorage = new ArrayList<>();
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
                var dependencies = getBeanDependencies(result, constructor);
                var container = new TypeMetadataContainer.Builder()
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
            throw new BeanDefinitionCreateException("Pipeline phase 1 failed: ClassGraph crashed while scanning package " + packageToScan, e);
        }
    }

    private Class<?> validateClass(Class<?> clazz) {
        if (clazz.isEnum() ||
                clazz.isInterface() ||
                clazz.isAnnotation() ||
                Modifier.isAbstract(clazz.getModifiers())) {
            throw new BeanDefinitionCreateException("Pipeline phase 1 failed: invariant violated. Invalid annotated type " + clazz.getName());
        }
        if (clazz.getDeclaredConstructors().length == 0) {
            throw new BeanDefinitionCreateException("Pipeline phase 1 failed: constructor is absent in class " + clazz.getName());
        }
        return clazz;
    }

    private Constructor<?> getTheOnlyConstructor(Class<?> sourceClass) {
        return Stream.of(sourceClass.getDeclaredConstructors())
                .reduce((first, second) -> {
                    throw new BeanDefinitionCreateException("Pipeline phase 1 failed: there is more than 1 constructor in class: " + sourceClass.getName());
                })
                .orElseThrow(() -> new BeanDefinitionCreateException("Pipeline phase 1 failed: there is no constructor in class: " + sourceClass.getName()));
    }

    private List<Class<?>> getBeanDependencies(ScanResult result, Constructor<?> constructor) {
        //TODO Add validation by result
        return Arrays.asList(constructor.getParameterTypes());
    }

}
