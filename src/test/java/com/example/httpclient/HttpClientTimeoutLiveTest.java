package com.example.httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpClientTimeoutLiveTest {

    private CloseableHttpResponse response;

    @After
    public final void after() throws IllegalStateException, IOException {
        ResponseUtil.closeResponse(response);
    }

    // tests
    @Test
    public final void givenUsingOldApi_whenSettingTimeoutViaParameter_thenCorrect() throws IOException {
        //قبل از HttpClient 4.3 برای set کردن timeout به روش زیر انجام میشود
        DefaultHttpClient httpClient = new DefaultHttpClient();
        int timeout = 5; // seconds
        HttpParams httpParams = httpClient.getParams();
        httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout * 1000);
        httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout * 1000);
        httpParams.setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(timeout * 1000));
        
        final HttpGet request = new HttpGet("http://www.github.com");
        HttpResponse execute = httpClient.execute(request);
        assertThat(execute.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public final void givenUsingNewApi_whenSettingTimeoutViaRequestConfig_thenCorrect() throws IOException {
        final int timeout = 2;
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();
        final CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        final HttpGet request = new HttpGet("http://www.github.com");

        response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public final void givenUsingNewApi_whenSettingTimeoutViaSocketConfig_thenCorrect() throws IOException {
        final int timeout = 2;

        final SocketConfig config = SocketConfig.custom().setSoTimeout(timeout * 1000).build();
        final CloseableHttpClient client = HttpClientBuilder.create().setDefaultSocketConfig(config).build();

        final HttpGet request = new HttpGet("http://www.github.com");

        response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    @Test
    public final void givenUsingNewApi_whenSettingTimeoutViaHighLevelApi_thenCorrect() throws IOException {
        final int timeout = 5;
        //ست کردن timeout با fluent api
        //پیشنهاد میشه از این روش استفاده شود که هم امن است و هم خوانایی آن بالا است

        final RequestConfig config = RequestConfig.custom()
                //این پارامتر مهم هست و باید در نظر گرفته شود
                .setConnectTimeout(timeout * 1000)//زمان برقراری ارتباط با سرور ریموت
                //این پارامتر را میتوان نادیده گرفت
                .setConnectionRequestTimeout(timeout * 1000)//زمان انتظار برای گرفتن connection از connetion pool
                //این پارامتر مهم است و باید در نظر گرفته شود
                .setSocketTimeout(timeout * 1000).build();//زمان انتظار برای داده ها-پس از ایجاد اتصال حداکثر زمان عدم فعالیت بین data packet ها
        final CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        final HttpGet request = new HttpGet("http://www.github.com");
//خطای connection timeout برابر با org.apache.http.conn.ConnectTimeoutException است
        //خطای socket timeOut برابر با  java.net.SocketTimeoutException. است
        response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    }

    /**
     * This simulates a timeout against a domain with multiple routes/IPs to it (not a single raw IP)
     */
    @Test(expected = ConnectTimeoutException.class)
    @Ignore
    public final void givenTimeoutIsConfigured_whenTimingOut_thenTimeoutException() throws IOException {
        //timeOut and DNS Round Robin
        //مینوان یک domain را بر روی ip های مختلف route کنیم
        //به این صورت عمل میکند یک لیست از ip ها را دریافت میکند اولین مورد را امتحان میکند که تمام میشود(یک وقفه زمانی هم در کانفیگ کردن برای آن در نظر گرفته میشود
        //بعد میرود سراغ ip بعدی و به همین ترتیب ادامه میدهد
        //عملیات کلی زمانی تمام میشود که تمام ip های ممکن به پایان رسیده باشد
        final int timeout = 3;

        final RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
        final CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        final HttpGet request = new HttpGet("http://www.google.com:81");
        client.execute(request);
    }
    
    @Test
    public void whenSecuredRestApiIsConsumed_then200OK() throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        int timeout = 20; // seconds
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout * 1000)
          .setConnectTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
        HttpGet getMethod = new HttpGet("http://localhost:8082/httpclient-simple/api/bars/1");
        getMethod.setConfig(requestConfig);
        //با استفاده از timerTask و timer یک delay ساده با abortکردن درخواست http get request
        int hardTimeout = 5; // seconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getMethod.abort();
            }
        };
        new Timer(true).schedule(task, hardTimeout * 1000);

        HttpResponse response = httpClient.execute(getMethod);
        System.out.println("HTTP Status of response: " + response.getStatusLine().getStatusCode());
    }
    
}
