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
                                
            Solution: Create implementation for type (may be forgot annotation @Managed).
                                
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

    public static final String MISSING_CONSTRUCTOR_ERROR_TEMPLATE = """
            
                                
            [EasySpring BeanDefinition configuration failure]: Multiple implementations for type
            │
            └──> Type:
                  └──> %s
                                
            Solution: Provide constructor for type.
                                
            """;

    public static final String SCOPED_TARGET_PROBLEM_ERROR_TEMPLATE = """
            
                                
            [EasySpring BeanDefinition configuration failure]: Scoped target problem detected
            │
            ├──> Singleton scope type:
            │      └──> %s
            │      
            └──> OneOff scope dependency:
                  └──> %s
                                
            Solution: We recommend to use provider pattern to inject OneOff bean in singleton.
                                
            """;
}