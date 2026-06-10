package com.github.codedissection.easyspring.bootstrap;

import com.github.codedissection.easyspring.bootstrap.dto.MetadataContainer;
import com.github.codedissection.easyspring.definition.BeanDefinition;
import com.github.codedissection.easyspring.definition.annotation.root.EasySpringAnnotation;
import com.github.codedissection.easyspring.definition.exception.BeanDefinitionCreateException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class PipelineWorker {

    public List<MetadataContainer> getMetadataConfiguration(String packageToScan) {
        List<MetadataContainer> classMetadataStorage = new ArrayList<>();
        ClassGraph scanner = new ClassGraph()
                .acceptPackages(packageToScan)
                .enableClassInfo()
                .enableAnnotationInfo();

        try (ScanResult result = scanner.scan()) {
            var classes = result.getClassesWithAnnotation(EasySpringAnnotation.class.getName());
            for (ClassInfo info : classes) {
                var sourceClass = validateClass(info.loadClass());
                var className = info.getName();
                var constructor = getTheOnlyConstructor(sourceClass);
                var dependencies = getBeanDependencies(result, constructor);
                var container = new MetadataContainer.Builder()
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

    public Map<String, BeanDefinition> createDefinitions(List<MetadataContainer> loadClasses) {
        Map<String, BeanDefinition> definitionStorage = new ConcurrentHashMap<>();
        for (MetadataContainer myClass : loadClasses) {
            var name = myClass.getName();
            var sourceClass = myClass.getSourceClass();
            var dependencies = myClass.getDependencies();
            var bd = new BeanDefinition.Builder(sourceClass, dependencies)
                    .build();
            definitionStorage.put(name, bd);
        }
        return definitionStorage;
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
        return Arrays.stream(constructor.getParameterTypes())
                .map(type -> {
                         Class<?> implementation;
                         if (type.isInterface()) {
                             var implementations = result.getClassesImplementing(type.getName()).stream()
                                     .filter(impl -> impl.hasAnnotation(EasySpringAnnotation.class.getName()))
                                     .toList();
                             if (implementations.size() > 1) {
                                 throw new BeanDefinitionCreateException("Pipeline phase 1 failed: interface " + type.getName() + " has more than 1 implementations");
                             }
                             if (implementations.isEmpty()) {
                                 throw new BeanDefinitionCreateException("Pipeline phase 1 failed: interface " + type.getName() + " has no implementations");
                             }
                             implementation = implementations.getFirst().loadClass();
                         } else {
                             implementation = type;
                         }
                         var shouldUse = implementation.isAnnotationPresent(EasySpringAnnotation.class);
                         return new DependencyResolution(implementation, shouldUse);
                     }
                )
                .filter(dependencyResolution -> dependencyResolution.shouldUse)
                .<Class<?>>map(dependencyResolution -> dependencyResolution.implementation)
                .toList();
    }

    private record DependencyResolution(Class<?> implementation, boolean shouldUse) {
    }
}