package com.example.client;;

import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

/**
 * created by Atiye Mousavi
 * Date: 9/23/2021
 * Time: 1:56 PM
 */
public class HttpComponentsClientHttpRequestFactoryBasicAuth extends HttpComponentsClientHttpRequestFactory {
    HttpHost host;

    public HttpComponentsClientHttpRequestFactoryBasicAuth( HttpHost host) {
        super();
        this.host = host;
    }
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return createHttpContext();
    }

    private HttpContext createHttpContext(){
        AuthCache authCache=new BasicAuthCache();
        BasicScheme basicScheme=new BasicScheme();
        authCache.put(host,basicScheme);

        BasicHttpContext localContext=new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.AUTH_CACHE,authCache);
        return localContext;
    }
}
