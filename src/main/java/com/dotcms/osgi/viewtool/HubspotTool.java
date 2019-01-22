package com.dotcms.osgi.viewtool;

import com.dotcms.osgi.api.HubspotAPI;

import com.dotmarketing.util.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

public class HubspotTool implements ViewTool {
    private HttpServletRequest request;

    @Override
    public void init(Object initData) {

        ViewContext context = (ViewContext) initData;
        request = context.getRequest();

    }

    /**
     * Caches contact information in the user session
     * 
     * @return
     */
    public JSONObject getContact() {
        return new HubspotAPI().getContact(request);
    }

    public JSONObject refreshContact() {
        return new HubspotAPI().refreshContact(request);
    }
    
    
    public int score() {

        try {
            return Integer.parseInt(getContact().get("properties.hubspotscore").toString());
        }
        catch(Exception e) {
            return 0;
        }
        
        
        
        
        
    }
    
    
    

}
