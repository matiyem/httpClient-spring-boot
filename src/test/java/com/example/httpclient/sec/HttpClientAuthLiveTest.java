package com.example.httpclient.sec;

import com.example.httpclient.ResponseUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/*
 * NOTE : Need module httpclient-simple to be running
 */

public class HttpClientAuthLiveTest {

    private static final String URL_SECURED_BY_BASIC_AUTHENTICATION = "http://localhost:8082/httpclient-simple/api/foos/1";
    private static final String DEFAULT_USER = "user1";
    private static final String DEFAULT_PASS = "user1Pass";

    private CloseableHttpClient client;

    private CloseableHttpResponse response;

    @Before
    public final void before() {
        client = HttpClientBuilder.create().build();
    }

    @After
    public final void after() throws IllegalStateException, IOException {
        ResponseUtil.closeResponse(response);
    }

    // tests

    @Test
    public final void whenExecutingBasicGetRequestWithBasicAuthenticationEnabled_thenSuccess() throws IOException {
        //در اینجا در حال set کردن basic authentication میباشیم
        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider()).build();

        response = client.execute(new HttpGet(URL_SECURED_BY_BASIC_AUTHENTICATION));

        final int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, equalTo(HttpStatus.SC_OK));
    }

    @Test
    public final void givenAuthenticationIsPreemptive_whenExecutingBasicGetRequestWithBasicAuthenticationEnabled_thenSuccess() throws IOException {
        client = HttpClientBuilder.create().build();
        response = client.execute(new HttpGet(URL_SECURED_BY_BASIC_AUTHENTICATION), context());

        final int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, equalTo(HttpStatus.SC_OK));
    }

    @Test
    public final void givenAuthorizationHeaderIsSetManually_whenExecutingGetRequest_thenSuccess() throws IOException {
        //در اینجا the pre-authentication را ارسال میکنیم
        //همه چیز خوب به نظر می رسد:
        //
        //طرح "احراز هویت اساسی" از قبل انتخاب شده است
        //درخواست با Authorization header ارسال می شود
        //سرور با 200 OK پاسخ می دهد
        //احراز هویت موفق می شود
        client = HttpClientBuilder.create().build();

        final HttpGet request = new HttpGet(URL_SECURED_BY_BASIC_AUTHENTICATION);
        request.setHeader(HttpHeaders.AUTHORIZATION, authorizationHeader(DEFAULT_USER, DEFAULT_PASS));
        response = client.execute(request);

        final int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, equalTo(HttpStatus.SC_OK));
    }

    @Test
    public final void givenAuthorizationHeaderIsSetManually_whenExecutingGetRequest_thenSuccess2() throws IOException {
        //احراز هویت اولیه پیشگیرانه اساساً به معنای پیش ارسال Authorization header مجوز است.
        //
        //بنابراین ، به جای گذراندن مثال پیچیده قبلی برای راه اندازی ، می توانیم کنترل این سرصفحه را در دست بگیریم و آن را با دست بسازیم:
        //بنابراین ، با وجود اینکه هیچ cache وجود ندارد ، احراز هویت اساسی هنوز به درستی کار می کند و ما 200 OK دریافت می کنیم.
        final HttpGet request = new HttpGet(URL_SECURED_BY_BASIC_AUTHENTICATION);
        final String auth = DEFAULT_USER + ":" + DEFAULT_PASS;
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        final String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        client = HttpClientBuilder.create().build();
        response = client.execute(request);

        final int statusCode = response.getStatusLine().getStatusCode();
        assertThat(statusCode, equalTo(HttpStatus.SC_OK));
    }

    // UTILS

    private CredentialsProvider provider() {
        //یکی از راه های استانداردbasic authentication استفاده از CredentialsProvider است
//کل ارتباط مشتری و سرور اکنون روشن است:
//
//مشتری درخواست HTTP را بدون اعتبار ارسال می کند
//سرور یک چالش را ارسال می کند
//مشتری مذاکره می کند و طرح احراز هویت مناسب را مشخص می کند
//مشتری درخواست دوم را ارسال می کند ، این بار با اعتبارنامه
        final CredentialsProvider provider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(DEFAULT_USER, DEFAULT_PASS);
        provider.setCredentials(AuthScope.ANY, credentials);
        return provider;
    }

    private HttpContext context() {
        //این یک Preemptive Basic Authentication
        //در اینجا داریم یک httpClient را با cache احراز هویت پر میکنیم
        final HttpHost targetHost = new HttpHost("localhost", 8082, "http");
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(DEFAULT_USER, DEFAULT_PASS));

        // Create AuthCache instance
        final AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        authCache.put(targetHost, new BasicScheme());

        // Add AuthCache to the execution context
        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        return context;
    }

    private String authorizationHeader(final String username, final String password) {
        final String auth = username + ":" + password;
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));

        return "Basic " + new String(encodedAuth);
    }

}
