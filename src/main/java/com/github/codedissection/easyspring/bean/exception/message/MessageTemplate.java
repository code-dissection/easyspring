package com.github.codedissection.easyspring.bean.exception.message;

public class MessageTemplate {
    public static final String MULTIPLE_CONSTRUCTORS_ERROR_TEMPLATE = """
            
                                
            [EasySpring Bean configuration mistake]: Couldn't choose constructor for injection!
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
}