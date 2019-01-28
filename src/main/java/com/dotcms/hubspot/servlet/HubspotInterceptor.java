
package com.dotcms.hubspot.servlet;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.dotcms.filters.interceptor.Result;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotcms.hubspot.api.HubspotAPI;
import com.dotmarketing.util.json.JSONException;
import com.dotmarketing.util.json.JSONObject;

public class HubspotInterceptor implements WebInterceptor{


    private final static String hubspotPostingUrl = "/hubapi";
    private final static String[] filters = {hubspotPostingUrl};
    private final HubspotAPI hubapi = new HubspotAPI();
    
    
    
    
    @Override
    public final String[] getFilters() {
        return filters;
    }
    
    @Override
    public String getName() {
        return this.getClass().getName();
    }


    @Override
    public Result intercept(final HttpServletRequest request,
                            final HttpServletResponse response) throws IOException {
        
        
        System.out.println("we got a hub request");
        
        
        try {

            final String uri = request.getRequestURI();
            final String hubUrl = uri.replaceAll(hubspotPostingUrl, "");
            System.err.println("hubspot:" + hubUrl);
            
            
            
            if("/me".equals(hubUrl)) {
                response.getWriter().write(hubapi.refreshContact(request).toString(2));
            }else if ("GET".equals(request.getMethod())) {
                IOUtils.write(hubapi.proxyGet(hubUrl), response.getOutputStream());
                response.flushBuffer();

            } else if ("POST".equals(request.getMethod())) {
                IOUtils.write(hubapi.proxyPost(hubUrl, decodeRequest(request)), response.getOutputStream());
                response.flushBuffer();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.SKIP_NO_CHAIN;




    }

    private JSONObject decodeRequest(HttpServletRequest request) throws IOException, JSONException {
        String x = IOUtils.toString(request.getReader());


        return new JSONObject(x);
    }

    public void destroy() {
        System.out.println("HubspotFilter Filter Destroyed");
    }



}
