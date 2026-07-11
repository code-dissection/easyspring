package com.github.codedissection.easyspring.definition.exception.message;

public class MessageTemplate {

    public static final String RESOLVED_IMPLEMENTATION_ERROR_TEMPLATE = """
                                
            [EasySpring BeanDefinition configuration failure]: Resolved implementation is null
            │
            ├──> Type:
            │     └──> %s
            │
            └──> Type resolution:
                  └──> null 
                                
            Solution: Create implementation for type.
                                
            """;

    public static final String MULTIPLE_IMPLEMENTATIONS_ERROR_TEMPLATE = """
                                
            [EasySpring BeanDefinition configuration failure]: Multiple implementations for type
            │
            ├──> Type:
            │     └──> %s
            │
            ├──> New type implementation:
            │     └──> %s
            │
            └──> Already exists type implementation:
                  └──> %s 
                                
            Solution: Resolve implementations for type.
                                
            """;
}