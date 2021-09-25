package com.example.client;

import org.apache.http.HttpHost;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * created by Atiye Mousavi
 * Date: 9/24/2021
 * Time: 11:26 AM
 */
@Component
public class RestTemplateFactory implements FactoryBean<RestTemplate> , InitializingBean {
    private RestTemplate restTemplate;
    @Override
    public RestTemplate getObject()  {
        return restTemplate;
    }

    @Override
    public Class<RestTemplate> getObjectType() {
        return RestTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        HttpHost host=new HttpHost("localhost",8082,"http");
        final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(host);
        restTemplate=new RestTemplate(requestFactory);
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("user1","user1Pass"));
    }

}
