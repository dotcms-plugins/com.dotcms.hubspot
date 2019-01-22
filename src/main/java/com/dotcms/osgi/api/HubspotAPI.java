package com.dotcms.osgi.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.json.JSONObject;
import com.google.gson.Gson;

public class HubspotAPI {
    private final String HSKEY = "hapikey=a4abf0e7-48ae-44ba-84c2-c44fde2b3d5e";
    private final String HUBHOST = "https://api.hubapi.com/";
    private final String GET_CONTACT_URL = "/contacts/v1/contact/utk/HSCOOKIE/profile";
    public final static String HUB_COOKIE = "hubspotutk";

  private final String[] validContactFields = {"email", "firstname", "lastname", "leadsource", "gdpr_acceptance",
      "gdpr_opt_in_single_check", "message", "industry", "tags", "hs_persona", "numemployees", "website", "phone", "address", "city",
      "state", "zip", "company", "country", "lead_source_category"};


    private String getHubUrl(final String incoming) {



        return HUBHOST + ((incoming.indexOf("hapikey=") > -1) ? incoming
                : (incoming.indexOf("?") > -1) ? incoming + "&" + HSKEY : incoming + "?" + HSKEY);
    }

    public String proxyPost(final String incomingUrl, JSONObject json) throws ClientProtocolException, IOException {

        final String huburl = getHubUrl(incomingUrl);
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(huburl);
        System.err.println("proxyPost.json" + json);
        StringEntity postingString = new StringEntity(json.toString());
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);

        if (response != null) {
            return IOUtils.toString(response.getEntity().getContent()); // Get the data in the //
                                                                        // entity
        }
        return gson.toJson(response.getStatusLine());
    }

    public String proxyGet(final String incomingUrl) throws ClientProtocolException, IOException {
        Gson gson = new Gson();

        final String huburl = getHubUrl(incomingUrl);
        System.err.println("huburl:" + huburl);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(huburl);

        HttpResponse response = httpClient.execute(get);

        if (response != null) {
            return IOUtils.toString(response.getEntity().getContent()); // Get the data in the
                                                                        // entity
        }
        return gson.toJson(response.getStatusLine());
    }

    public JSONObject getContact(final HttpServletRequest request) {
        final String hubspotutk = getHubCookie(request);
        if (hubspotutk != null && request.getSession(false) != null  && request.getSession().getAttribute(HubspotAPI.HUB_COOKIE) == null) {
            final String url = GET_CONTACT_URL.replaceAll("HSCOOKIE", hubspotutk);
            synchronized (hubspotutk) {
                if (request.getSession().getAttribute(HubspotAPI.HUB_COOKIE) == null) {
                    try {
                        request.getSession().setAttribute(HubspotAPI.HUB_COOKIE, new JSONObject(this.proxyGet(url)));
                    } catch (Exception e) {
                        Logger.warn(this.getClass(), e.getMessage(), e);
                    }
                }
            }
        }
        return (JSONObject) request.getSession().getAttribute(HubspotAPI.HUB_COOKIE);
    }
    
    public JSONObject refreshContact(final HttpServletRequest request) {
        
        request.getSession().removeAttribute(HubspotAPI.HUB_COOKIE);
        return getContact(request);
    }
    
    public String getHubCookie(final HttpServletRequest request) {

        return UtilMethods.getCookieValue(request.getCookies(), HUB_COOKIE);

    }

}
