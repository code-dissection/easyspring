package com.github.codedissection.easyspring.bootstrap;

import com.github.codedissection.easyspring.bootstrap.dto.ClassPropertiesContainer;
import com.github.codedissection.easyspring.definition.BeanDefinition;
import com.github.codedissection.easyspring.definition.annotation.root.EasySpringAnnotation;
import com.github.codedissection.easyspring.definition.exception.BeanDefinitionCreationException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PipelineWorker {

    public List<ClassPropertiesContainer> scanAppCode(String packageToScan) {
        ClassGraph scanner = new ClassGraph()
                .acceptPackages(packageToScan)
                .enableClassInfo()
                .enableAnnotationInfo();

        try (ScanResult result = scanner.scan()) {
            var classes = result.getClassesWithAnnotation(EasySpringAnnotation.class.getName())
                    .filter(classInfo -> classInfo.getPackageName().startsWith(packageToScan));
            List<ClassPropertiesContainer> classPropertiesStorage = new ArrayList<>();
            for (ClassInfo info : classes) {
                var sourceClass = validateClass(info.loadClass());
                var className = info.getName();
                var constructors = sourceClass.getDeclaredConstructors();
                var dependencies = Arrays.stream(constructors[0].getParameterTypes())
                        .toList(); //todo Suppose there is the only constructor in class
                var container = new ClassPropertiesContainer.Builder()
                        .withName(className)
                        .withSourceClass(sourceClass)
                        .withDependencies(dependencies)
                        .build();
                classPropertiesStorage.add(container);
            }
            return classPropertiesStorage;
        } catch (BeanDefinitionCreationException e) {
            throw e;
        } catch (Exception e) {
            throw new BeanDefinitionCreationException("Pipeline phase 1 failed: ClassGraph crashed while scanning package " + packageToScan, e);
        }
    }

    public Map<String, BeanDefinition> createDefinitions(List<ClassPropertiesContainer> loadClasses) {
        Map<String, BeanDefinition> definitionStorage = new ConcurrentHashMap<>();
        for (ClassPropertiesContainer myClass : loadClasses) {
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
            throw new BeanDefinitionCreationException("Pipeline phase 1 failed: invariant violated. Invalid annotated type " + clazz.getName());
        }
        if (clazz.getDeclaredConstructors().length == 0) {
            throw new BeanDefinitionCreationException("Pipeline phase 1 failed: constructor is absent in class " + clazz.getName());
        }
        return clazz;
    }
}