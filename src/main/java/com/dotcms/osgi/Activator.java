/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dotcms.osgi;

import com.dotcms.osgi.servlet.HubspotFilter;
import com.dotcms.osgi.util.FilterOrder;
import com.dotcms.osgi.util.TomcatServletFilterUtil;
import com.dotcms.osgi.viewtool.HubspotToolInfo;

import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Logger;

import org.osgi.framework.BundleContext;

public final class Activator extends GenericBundleActivator {

    final static String FILTER_NAME = "HubspotFilter";



    @Override
    public void start(BundleContext context) throws Exception {
        initializeServices(context);
        new TomcatServletFilterUtil().addFilter(FILTER_NAME, new HubspotFilter(), FilterOrder.FIRST, "*");


        // Registering the ViewTool service
        registerViewToolService(context, new HubspotToolInfo());

        Logger.info(this.getClass(), "Starting HubSpotTool");

    }


    @Override
    public void stop(BundleContext context) throws Exception {
        unregisterViewToolServices();
        new TomcatServletFilterUtil().removeFilter(FILTER_NAME);



    }
}
