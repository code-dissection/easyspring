package com.github.codedissection.easyspring.scanner;

import com.github.codedissection.easyspring.scanner.annotation.root.EasySpringAnnotation;
import com.github.codedissection.easyspring.scanner.exception.ExternalLibraryException;
import com.github.codedissection.easyspring.scanner.exception.MultipleConstructorsException;
import com.github.codedissection.easyspring.scanner.exception.ProjectScannerException;
import com.github.codedissection.easyspring.scanner.exception.TypeInvariantViolationException;
import com.github.codedissection.easyspring.scanner.model.TypeMetadata;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.codedissection.easyspring.scanner.exception.message.MessageTemplate.CLASS_GRAPH_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.scanner.exception.message.MessageTemplate.COMPONENT_TYPE_ERROR_TEMPLATE;
import static com.github.codedissection.easyspring.scanner.exception.message.MessageTemplate.MULTIPLE_CONSTRUCTORS_ERROR_TEMPLATE;

public class ProjectScanner {

    public Set<TypeMetadata> getProjectConfiguration(String packageToScan) {
        var typeMetadataStorage = new HashSet<TypeMetadata>();
        var scanner = new ClassGraph()
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
                var constructor = getTheOnlyConstructor(sourceClass);
                var dependencies = getBeanDependencies(constructor);
                var container = new TypeMetadata(
                        sourceClass,
                        dependencies
                );
                typeMetadataStorage.add(container);
            }
            return typeMetadataStorage;
        } catch (ProjectScannerException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalLibraryException(String.format(
                    CLASS_GRAPH_ERROR_TEMPLATE,
                    packageToScan,
                    e
            ));
        }
    }

    private Class<?> validateClass(Class<?> clazz) {
        if (clazz.isEnum() ||
            clazz.isInterface() ||
            clazz.isAnnotation() ||
            Modifier.isAbstract(clazz.getModifiers())) {
            throw new TypeInvariantViolationException(String.format(
                    COMPONENT_TYPE_ERROR_TEMPLATE,
                    clazz.getName()
            ));
        }
        return clazz;
    }

    private Constructor<?> getTheOnlyConstructor(Class<?> sourceClass) {
        var constructors = sourceClass.getDeclaredConstructors();
        if (constructors.length > 1) {
            throw new MultipleConstructorsException(String.format(
                    MULTIPLE_CONSTRUCTORS_ERROR_TEMPLATE,
                    sourceClass.getName())
            );
        }
        return constructors[0];
    }

    private List<Class<?>> getBeanDependencies(Constructor<?> constructor) {
        return Arrays.asList(constructor.getParameterTypes());
    }

}
