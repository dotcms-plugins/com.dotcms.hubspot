package com.dotcms.osgi.viewtool;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;

public class HubspotToolInfo extends ServletToolInfo {

    @Override
    public String getKey () {
        return "hubspot";
    }

    @Override
    public String getScope () {
        return ViewContext.REQUEST;
    }

    @Override
    public String getClassname () {
        return HubspotTool.class.getName();
    }

    @Override
    public Object getInstance ( Object initData ) {

        HubspotTool viewTool = new HubspotTool();
        viewTool.init( initData );

        setScope( ViewContext.REQUEST );

        return viewTool;
    }

}