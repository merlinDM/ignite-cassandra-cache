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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IgniteCassandra {

    final static Logger logger = LogManager.getLogger(IgniteCassandra.class);

    public static void main(String[] args) throws IOException {

        // Handles the configuration configuration
        Properties conf = getConfiguration();

        IgniteConfiguration cfg = new IgniteConfiguration();
        CacheConfiguration configuration = new CacheConfiguration();

        // Setting cache name
        configuration.setName("ignite-cassandra");

        // Configuring Cassandra's persistence
        DataSource dataSource = new DataSource();
        dataSource.setContactPoints(conf.getProperty("cassandra.contactPoints"));

        RoundRobinPolicy robinPolicy = new RoundRobinPolicy();

        dataSource.setLoadBalancingPolicy(robinPolicy);
        dataSource.setReadConsistency("ONE");
        dataSource.setWriteConsistency("ONE");

        String persistenceSettingsXml = conf.getProperty("ignite.persistenceSettings");
        KeyValuePersistenceSettings persistenceSettings = new KeyValuePersistenceSettings(persistenceSettingsXml);

        CassandraCacheStoreFactory cacheStoreFactory = new CassandraCacheStoreFactory();
        cacheStoreFactory.setDataSource(dataSource);
        cacheStoreFactory.setPersistenceSettings(persistenceSettings);
        configuration.setCacheStoreFactory(cacheStoreFactory);
        configuration.setWriteThrough(true);
        configuration.setWriteBehindEnabled(true);
        configuration.setReadThrough(true);

        // Sets the cache configuration
        cfg.setCacheConfiguration(configuration);

        // Starting Ignite
        Ignite ignite = Ignition.start(cfg);
    }

    // @throws IgniteException
    public static KeyValuePersistenceSettings getPersistenceSettings(String filename) {
        File file = new File(filename);        
        KeyValuePersistenceSettings settings = new KeyValuePersistenceSettings(file);
        return settings; 
    }

    public static Properties getConfiguration() {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("ignite-cassandra.properties");
            properties.load(input);
        } catch (IOException ex) {
            logger.error("Cannot load configuration");
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
