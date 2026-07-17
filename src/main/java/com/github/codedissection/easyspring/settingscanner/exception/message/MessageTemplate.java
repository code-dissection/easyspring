package com.github.codedissection.easyspring.settingscanner.exception.message;

public final class MessageTemplate {

    public static final String PARSING_YAML_ERROR_TEMPLATE = """
            
                                
            [EasySpring Settings scanner failure]: Settings file parser crashed
            │
            ├──> Settings file:
            │     └──>%s
            │
            └──> Real reason:
                  └──> %s          
                                
            Solution: Fix file structure.
                                
            """;


}
