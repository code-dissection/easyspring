package com.github.codedissection.easyspring.context.exception.message;

public class MessageTemplate {

    public static final String MULTIPLE_CLOSE_METHODS_ERROR_TEMPLATE = """
            
                                            
            [EasySpring Shutdown project failure]: Multiple close methods detected
            │
            ├──> Type:
            │     └──> %s
            │
            └──> Methods:
                  └──> %s 
                                
            Solution: Leave the only close method.
                                
            """;

    public static final String STATIC_CLOSE_METHOD_ERROR_TEMPLATE = """
            
                                            
            [EasySpring Shutdown project failure]: Static close method detected
            │
            ├──> Type:
            │     └──> %s
            │
            └──> Method:
                  └──> %s 
                                
            Solution: Remove the static modifier.
                                
            """;

    public static final String CLOSE_METHOD_ACCESS_ERROR_TEMPLATE = """
            
                                            
            [EasySpring Shutdown project warning]: There is no access to close method
            │
            ├──> Type:
            │     └──> %s
            │
            └──> Method:
                  └──> %s 
                                
            Solution: Change your environment or JVM security settings to allow.
                                
            """;

    public static final String INVOCATION_METHOD_ACCESS_ERROR_TEMPLATE = """
            
                                            
            [EasySpring Shutdown project warning]: Close method crashed during execution
            │
            ├──> Type:
            │     └──> %s
            │
            ├──> Method:
            │     └──> %s
            │
            └──> Real cause:
                  └──> %s
                                
            Solution: Read the exception stack trace below to fix your code.
                                
            """;

    public static final String PROJECT_SHUTDOWN_TEMPLATE = """
            
                                            
            [EasySpring Project is shutting down]: Project is shutting down
            │
            └──> Unavailable bean:
                  └──> %s 
                                
            Solution: Just relax.
                                
            """;

}