package com.example.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * created by Atiye Mousavi
 * Date: 9/24/2021
 * Time: 4:08 PM
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.example.web")
public class WebConfig implements WebMvcConfigurer {

    public WebConfig(){
        super();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }
}
