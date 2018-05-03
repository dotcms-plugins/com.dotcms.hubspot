
package com.dotcms.osgi.servlet;


import com.dotcms.osgi.api.HubspotAPI;
import com.dotcms.repackage.com.google.common.annotations.VisibleForTesting;
import com.dotcms.vanityurl.handler.VanityUrlHandlerResolver;

import com.dotmarketing.business.web.HostWebAPI;
import com.dotmarketing.business.web.LanguageWebAPI;
import com.dotmarketing.business.web.UserWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.filters.CMSUrlUtil;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.json.JSONException;
import com.dotmarketing.util.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class HubspotFilter implements Filter {

    private final CMSUrlUtil urlUtil;

    private final static String hubspotPostingUrl = "/hubAPI";
    private final HubspotAPI hubapi = new HubspotAPI();


    public HubspotFilter() {
        this(VanityUrlHandlerResolver.getInstance(), CMSUrlUtil.getInstance(), WebAPILocator.getHostWebAPI(),
                WebAPILocator.getLanguageWebAPI(), WebAPILocator.getUserWebAPI());
    }

    @VisibleForTesting
    protected HubspotFilter(final VanityUrlHandlerResolver vanityUrlHandlerResolver, final CMSUrlUtil urlUtil,
            final HostWebAPI hostWebAPI, final LanguageWebAPI languageWebAPI, final UserWebAPI userWebAPI) {



        this.urlUtil = urlUtil;

    }



    public void init(FilterConfig config) throws ServletException {
        System.out.println("HubspotFilter Filter Started");
    }

    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            String uri = this.urlUtil.getURIFromRequest(request);
            hubapi.getContact(request);

            if (uri.startsWith(hubspotPostingUrl)) {

                String hubUrl = uri.replaceAll(hubspotPostingUrl, "");
                System.err.println("hubspot:" + hubUrl);
                if ("GET".equals(request.getMethod())) {
                    IOUtils.write(hubapi.proxyGet(hubUrl), response.getOutputStream());
                    response.flushBuffer();
                    return;
                } else if ("POST".equals(request.getMethod())) {
                    IOUtils.write(hubapi.proxyPost(hubUrl, decodeRequest(request)), response.getOutputStream());
                    response.flushBuffer();
                    return;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        chain.doFilter(req, res);



    }

    private JSONObject decodeRequest(HttpServletRequest request) throws IOException, JSONException {
        String x = IOUtils.toString(request.getReader());


        return new JSONObject(x);
    }

    public void destroy() {
        System.out.println("HubspotFilter Filter Destroyed");
    }



}
