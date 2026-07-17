package com.github.codedissection.easyspring.bean.exception.message;

public class MessageTemplate {
    public static final String MULTIPLE_CONSTRUCTORS_ERROR_TEMPLATE = """
            
                                
            [EasySpring Bean creation failure]: Couldn't choose constructor for injection!
            │
            ├──> Source component class:
            │    └──> %s
            │
            └──> Constructors detected: %d
                                
            Solution: leave only ONE constructor, to allow framework resolve dependency.
                                
            """;

    public static final String INSTANTIATION_ERROR_TEMPLATE = """
            
                                
            [EasySpring Bean creation failure]: Couldn't instantiate bean!
            │
            ├──> Source bean class:
            │    └──> %s
            │
            ├──> Constructor requires:
            │    └──> %s
            │
            ├──> Constructor got:
            │    └──> %s
            │
            └──> Root cause of failure:
                 └──> %s
                                
            """;

    public static final String MULTIPLE_INIT_ANNOTATED_METHODS_ERROR_TEMPLATE = """
            
                                
            [EasySpring Bean creation failure]: Multiple @Init annotated methods!
            │
            ├──> Source component class:
            │    └──> %s
            │
            └──> Methods detected:
                 └──> %s
                                
            Solution: leave only ONE @Init annotated method.
                                
            """;

    public static final String INIT_METHOD_HAS_PARAMETERS_ERROR_TEMPLATE = """
            
                                
            [EasySpring Bean creation failure]: @Init annotated method has parameters
            │
            ├──> Source component class:
            │    └──> %s
            │
            └──> Method with parameters:
                 └──> %s
                                
            Solution: delete method parameters.
                                
            """;

    public static final String INVOCATION_TARGET_EXCEPTION_ERROR_TEMPLATE = """
            
                                
            [EasySpring Bean creation failure]: User code in @Init annotated method thrown exception
            │
            └──> Real cause:
                 └──> %s
                                
            Solution: We don't know how to fix.
                                
            """;

    public static final String ILLEGAL_ACCESS_EXCEPTION_ERROR_TEMPLATE = """
            
                                
            [EasySpring Bean creation failure]: Illegal method access error
            │
            └──> Method:
                 └──> %s
                                
            Solution: It seems environment settings restricts access to @Init method.
                                
            """;

    public static final String INCOMPATIBILITY_TYPES_ERROR_TEMPLATE = """
            
                                
            [EasySpring Bean creation failure]: Can't resolve type
            │
            ├──> Bean type:
            │     └──> %s
            │
            └──> Parameter type:
                  └──> %s 
                                
            Solution: Resolve incompatibility of constructor parameter type and settings type.
                                
            """;
}