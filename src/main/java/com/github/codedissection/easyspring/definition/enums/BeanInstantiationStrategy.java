package com.github.codedissection.easyspring.definition.enums;

/**
 * Instantiation strategy (or instantiation timing strategy).
 * Defines when the bean instance is physically constructed.
 * Responsible solely for the placement on the execution timeline.
 */
public enum BeanInstantiationStrategy {

    EAGER,
    LAZY,
    EVENT_DRIVEN
}
