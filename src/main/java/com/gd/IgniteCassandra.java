package com.gd;

import com.datastax.driver.core.policies.RoundRobinPolicy;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.store.cassandra.CassandraCacheStoreFactory;
import org.apache.ignite.cache.store.cassandra.datasource.DataSource;
import org.apache.ignite.cache.store.cassandra.persistence.KeyValuePersistenceSettings;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class IgniteCassandra {

    final static Logger logger = getLogger();
    public static final String APP_PROPERTIES_FILE = "ignite-cassandra.properties";
    public static final String CASSANDRA_CONTACT_POINTS = "cassandra.contactPoints";
    public static final String IGNITE_PERSISTENCE_SETTINGS = "ignite.persistenceSettings";
    public static final String CACHE_NAME = "ignite-cassandra";
    public static final String LOG4J_PROPERTIES = "log4j.properties";

    public static void main(String[] args) {
        Properties appConfig = getConfiguration(APP_PROPERTIES_FILE);
        CacheConfiguration cacheConfig = new CacheConfiguration();
        IgniteConfiguration igniteConfig = new IgniteConfiguration();

        cacheConfig.setName(CACHE_NAME);

        CassandraCacheStoreFactory cacheStoreFactory = new CassandraCacheStoreFactory();

        DataSource dataSource = new DataSource();
        dataSource.setContactPoints(appConfig.getProperty(CASSANDRA_CONTACT_POINTS));
        dataSource.setReadConsistency("ONE");
        dataSource.setWriteConsistency("ONE");
        dataSource.setUser("cassandra");
        dataSource.setPassword("cassandra");

        RoundRobinPolicy robinPolicy = new RoundRobinPolicy();
        dataSource.setLoadBalancingPolicy(robinPolicy);

        cacheStoreFactory.setDataSource(dataSource);

        String persistenceSettingsXml = appConfig.getProperty(IGNITE_PERSISTENCE_SETTINGS);
        KeyValuePersistenceSettings persistenceSettings = new KeyValuePersistenceSettings(persistenceSettingsXml);
        cacheStoreFactory.setPersistenceSettings(persistenceSettings);
        cacheConfig.setCacheStoreFactory(cacheStoreFactory);

        cacheConfig.setWriteThrough(true);
        cacheConfig.setWriteBehindEnabled(true);
        cacheConfig.setReadThrough(true);

        // Sets the cache configuration
        igniteConfig.setCacheConfiguration(cacheConfig);

        // Starting Ignite
        Ignite ignite = Ignition.start(igniteConfig);
    }

    private static Logger getLogger() {
        URL is = IgniteCassandra.class.getClassLoader().getResource(LOG4J_PROPERTIES);
        PropertyConfigurator.configure(is);
        return LogManager.getLogger(IgniteCassandra.class);
    }

    public static KeyValuePersistenceSettings getPersistenceSettings(String filename) throws URISyntaxException {
        URL url = IgniteCassandra.class.getClassLoader().getResource(filename);
        File file = new File(url.toURI());
        System.out.println("Loading persistence settings from " + file.getAbsolutePath());
        logger.info("Loading persistence settings from " + file.getAbsolutePath());
        KeyValuePersistenceSettings settings = new KeyValuePersistenceSettings(file);
        return settings;
    }

    public static Properties getConfiguration(String filename) {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = IgniteCassandra.class.getClassLoader().getResourceAsStream(filename);
            properties.load(input);
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
        return properties;
    }
}
