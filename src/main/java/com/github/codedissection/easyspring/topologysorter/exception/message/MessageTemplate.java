package com.github.codedissection.easyspring.topologysorter.exception.message;

public class MessageTemplate {

    public static final String CIRCULAR_DEPENDENCY_ERROR_TEMPLATE = """
            
                                
            [EasySpring Topology sorter failure]: Circular dependency detected
            │
            └──> Circular chain of dependencies:
                  └──> %s 
                                
            Solution: Resolve this architecture failure. 
                                
            """;
}