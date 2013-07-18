package com.cloudbees.gasp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.util.Properties;

public class Config implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class.getName());
    private static String key = "";
    private static String envBuildSecretDir = null;

    public String getKey() {
        return key;
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
            // 1. Jenkins build secret plugin
            if ((envBuildSecretDir = System.getenv("GCM_API_KEY_TEST")) != null) {
                LOGGER.debug("Getting Build Secret from: " + envBuildSecretDir);
                FileInputStream propFile = new FileInputStream(envBuildSecretDir + "/" + "gcm-api-key.env");
                Properties p = new Properties(System.getProperties());
                p.load(propFile);
                System.setProperties(p);
                key = System.getProperty("GCM_API_KEY");
                LOGGER.debug("GCM_API_KEY (from Build Secret): " + System.getProperty("GCM_API_KEY"));
            }
            // 2. System property
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
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void contextDestroyed(ServletContextEvent event) {
    }
}