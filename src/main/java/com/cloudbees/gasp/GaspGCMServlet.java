/*
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudbees.gasp;

import com.cloudbees.gasp.services.DataSyncService;
import com.cloudbees.gasp.services.GCMRegistrationService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import java.io.InputStream;

public class GaspGCMServlet extends GuiceServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(GaspGCMServlet.class.getName());
    private static final String CLOUDBEES_APP_MODEL = "/WEB-INF/cloudbees-app-model.json";

    // GCM API Key
    private static String key = "";
    public static String getKey() {
        return key;
    }

    static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Initialize servlet with GCM API Key: for convenience, we will load this from system property,
     * environment variable or via the Jenkins build secret plugin (upload file gcm-api-key.env). In
     * each case the property/variable is GCM_API_KEY
     * Get the API key from http://code.google.com/apis/console
     *
     * @param event
     */
    public void contextInitialized(ServletContextEvent event) {
        // Get GCM_API_KEY from :
        try {
            // 1. System property
            if ((key = System.getProperty("GCM_API_KEY")) != null) {
                LOGGER.debug("GCM_API_KEY (from system property): " + key);
            }

            // 3. System environment
            else if ((key = System.getenv("GCM_API_KEY")) != null){
                LOGGER.debug("GCM_API_KEY (from system environment): " + key);
            }

            // Error: GCM_API_KEY not set
            else {
                LOGGER.error("GCM_API_KEY not set");
            }
            // CLOUDBEES_APP_MODEL is used by WEAVEFilter/WEAVEHook
            InputStream runAppModelJson = event.getServletContext().getResourceAsStream(CLOUDBEES_APP_MODEL);
            String cloudbeesAppModel = convertStreamToString(runAppModelJson);
            LOGGER.info("CloudBees App Model: " + cloudbeesAppModel);
            runAppModelJson.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
            new JerseyServletModule() {
                @Override
                protected void configureServlets() {
                    bind(GCMRegistrationService.class);
                    bind(DataSyncService.class);

                    serve("/*").with(GuiceContainer.class);
                }
            }
        );
    }
}