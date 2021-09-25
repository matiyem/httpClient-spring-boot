package com.example.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * created by Atiye Mousavi
 * Date: 9/24/2021
 * Time: 3:21 PM
 */
public class MySavedRequestAwareAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        final SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }
        final String targetUrlParameter=getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || (targetUrlParameter !=null && StringUtils.hasText(request.getParameter(targetUrlParameter)))){
            requestCache.removeRequest(request,response);
            super.onAuthenticationSuccess(request,response,authentication);
            return;
        }
        clearAuthenticationAttributes(request);

        // Use the DefaultSavedRequest URL
        // final String targetUrl = savedRequest.getRedirectUrl();
        // logger.debug("Redirecting to DefaultSavedRequest Url: " + targetUrl);
        // getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    public void setRequestCache(final RequestCache requestCache) {
        this.requestCache = requestCache;
    }

}
