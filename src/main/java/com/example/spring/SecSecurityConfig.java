package com.example.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * created by Atiye Mousavi
 * Date: 9/24/2021
 * Time: 4:05 PM
 */
@Configuration
@ImportResource({"classpath:webSecurityConfig.xml"})
@ComponentScan("com.example.security")
public class SecSecurityConfig {

    public SecSecurityConfig(){
        super();
    }
}
