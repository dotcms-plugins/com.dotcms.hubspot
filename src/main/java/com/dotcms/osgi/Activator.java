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


import com.dotcms.filters.interceptor.FilterWebInterceptorProvider;
import com.dotcms.filters.interceptor.WebInterceptorDelegate;
import com.dotcms.osgi.servlet.HubspotInterceptor;
import com.dotcms.osgi.viewtool.HubspotToolInfo;
import com.dotmarketing.filters.AutoLoginFilter;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;

import org.osgi.framework.BundleContext;

public final class Activator extends GenericBundleActivator {



    final HubspotInterceptor hubspotInterceptor = new HubspotInterceptor();

    @Override
    public void start(BundleContext context) throws Exception {
        //initializeServices(context);
        final FilterWebInterceptorProvider filterWebInterceptorProvider = FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);

        delegate.addFirst(hubspotInterceptor);


        // Registering the ViewTool service
        registerViewToolService(context, new HubspotToolInfo());

        Logger.info(this.getClass(), "Starting HubSpotTool");

    }


    @Override
    public void stop(BundleContext context) throws Exception {
        unregisterViewToolServices();
        final FilterWebInterceptorProvider filterWebInterceptorProvider = FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);

        delegate.remove(hubspotInterceptor.getName(), true);


    }
}
