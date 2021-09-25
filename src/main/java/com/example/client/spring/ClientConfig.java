package com.example.client.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * created by Atiye Mousavi
 * Date: 9/23/2021
 * Time: 1:33 PM
 */
@Configuration
@ComponentScan("com.example.client")
public class ClientConfig {
    public ClientConfig(){
        super();
    }
}
