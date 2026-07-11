package com.github.codedissection.easyspring.scanner.exception.message;

public final class MessageTemplate {

    private MessageTemplate() {
        throw new UnsupportedOperationException("MessageTemplate is utility class and can't be instantiated");
    }

    public static final String MULTIPLE_CONSTRUCTORS_ERROR_TEMPLATE = """
                                
            [EasySpring Project scanner failure]: Multiple constructors for class
            │
            └──> Class:
                  └──> %s          
                                
            Solution: Leave only one constructor.
                                
            """;

    public static final String COMPONENT_TYPE_ERROR_TEMPLATE = """
                                
            [EasySpring Project scanner failure]: Type is not concrete class 
            │
            └──> Type:
                  └──> %s          
                                
            Solution: Change component type. Interfaces, enums, abstract classes and annotations can't be graph node.
                                
            """;

    public static final String CLASS_GRAPH_ERROR_TEMPLATE = """
                                
            [EasySpring Project scanner failure]: Class Graph library crashed while scanning project 
            │
            ├──> Package to scan:
            │     └──> %s
            │
            └──> Class graph exception
                  └──> %s     
                                
            Solution: We can't help with it.
                                
            """;
}
