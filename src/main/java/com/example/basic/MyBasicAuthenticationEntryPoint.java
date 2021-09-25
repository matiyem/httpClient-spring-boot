package com.example.basic;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * created by Atiye Mousavi
 * Date: 9/23/2021
 * Time: 12:16 PM
 */
@Component
public class MyBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void afterPropertiesSet() {
        setRealmName("atiye");
        super.afterPropertiesSet();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.addHeader("WWW-Authentication","Basic realm=\""+ getRealmName()+"\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final PrintWriter writer=response.getWriter();
        writer.println("HTTP Status " + HttpServletResponse.SC_UNAUTHORIZED);
        super.commence(request, response, authException);
    }
}
