package com.example.httpclient.sec;

import com.example.httpclient.ResponseUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpClientCookieLiveTest {

    private CloseableHttpClient instance;

    private CloseableHttpResponse response;
    
    private static Logger log = LoggerFactory.getLogger(HttpClientCookieLiveTest.class);

    @Before
    public final void before() {
        instance = HttpClientBuilder.create().build();
    }

    @After
    public final void after() throws IllegalStateException, IOException {
        ResponseUtil.closeResponse(response);
    }

    // tests

    @Test
    public final void whenSettingCookiesOnARequest_thenCorrect() throws IOException {
        //این یک کد سطح پایین است که کوکی را بر روی headerست میکند
        instance = HttpClientBuilder.create().build();
        final HttpGet request = new HttpGet("http://www.github.com");
        request.setHeader("Cookie", "JSESSIONID=1234");

        response = instance.execute(request);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public final void givenUsingDeprecatedApi_whenSettingCookiesOnTheHttpClient_thenCorrect() throws IOException {
        //در ورژن های قبلی httpClient ها بصورت مستقیم cookie را در httpClient ست میکردیم
        //region در این قسمت در حال ساخت یک custom cooki هستیم
        final BasicCookieStore cookieStore = new BasicCookieStore();
        final BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "1234");
        cookie.setDomain(".github.com");
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");

        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        //endregion
        
        DefaultHttpClient client = new DefaultHttpClient();
        client.setCookieStore(cookieStore);
        
        final HttpGet request = new HttpGet("https://www.github.com");

        response = (CloseableHttpResponse) client.execute(request);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public final void whenSettingCookiesOnTheHttpClient_thenCookieSentCorrectly() throws IOException {
        final BasicCookieStore cookieStore = new BasicCookieStore();
        final BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "1234");
        cookie.setDomain(".github.com");
        cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        //در این قسمت cookie ساخت شده را add میکنیم
        instance = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        //بدون تنظیم دامنه مناسب ، client به هیچ وجه کوکی را ارسال نمی کند!
        final HttpGet request = new HttpGet("http://www.github.com");

        response = instance.execute(request);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public final void whenSettingCookiesOnTheRequest_thenCookieSentCorrectly() throws IOException {

        final BasicCookieStore cookieStore = new BasicCookieStore();
        final BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "1234");
        cookie.setDomain(".github.com");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        instance = HttpClientBuilder.create().build();

        final HttpGet request = new HttpGet("http://www.github.com");
        //همچنین میتوانیم کوکی ها را بر روی request هم کانفیگ کنیم از کد زیر استفاده میکنیم
        final HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        // localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore); // before 4.3
        response = instance.execute(request, localContext);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

}
