package com.gd;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLogger {

    final static Logger logger = LogManager.getLogger(IgniteCassandra.class);
    private Properties properties = null;

    PropertiesLogger() {
        this.properties = new Properties();
    }

    public String getProperty(String key) {
        String value = this.properties.getProperty(key);
        logger.debug("Accessed key: " + key + ", returning value: " + value);
        return value;
    }

    public void load(String filename) {
        InputStream input = null;
        try {
            input = IgniteCassandra.class.getClassLoader().getResourceAsStream(filename);
            this.properties.load(input);
        } catch (IOException ex) {
            logger.error("Cannot load configuration from " + filename);
            logger.error(ex.getStackTrace().toString());
            System.exit(1);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.debug("loaded properties from " + filename);
        return;
    }

    public Boolean isEmpty() {
        return this.properties.isEmpty();
    }
}
