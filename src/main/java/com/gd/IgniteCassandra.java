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
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class IgniteCassandra {

    private static Logger logger = LogManager.getLogger(IgniteCassandra.class.getName());
    private static final String APP_PROPERTIES_FILE = "ignite-cassandra.properties";
    private static final String CASSANDRA_CONTACT_POINTS = "cassandra.contactPoints";
    private static final String CASSANDRA_USER_NAME = "cassandra.username";
    private static final String CASSANDRA_USER_PASSWORD = "cassandra.password";
    private static final String IGNITE_PERSISTENCE_SETTINGS = "ignite.persistenceSettings";
    private static final String CACHE_NAME = "ignite-cassandra";
    private static final String LOG4J_PROPERTIES = "log4j.properties";

    public static void main(String[] args) throws IOException, URISyntaxException {

        PropertiesLogger appConfig = new PropertiesLogger();
        appConfig.load(APP_PROPERTIES_FILE);

        CacheConfiguration cacheConfig = new CacheConfiguration();
        IgniteConfiguration igniteConfig = new IgniteConfiguration();

        cacheConfig.setName(CACHE_NAME);

        CassandraCacheStoreFactory cacheStoreFactory = new CassandraCacheStoreFactory();

        DataSource dataSource = new DataSource();
        dataSource.setContactPoints(appConfig.getProperty(CASSANDRA_CONTACT_POINTS));
        dataSource.setReadConsistency("ONE");
        dataSource.setWriteConsistency("ONE");
        dataSource.setUser(appConfig.getProperty(CASSANDRA_USER_NAME));
        dataSource.setPassword(appConfig.getProperty(CASSANDRA_USER_PASSWORD));

        RoundRobinPolicy robinPolicy = new RoundRobinPolicy();
        dataSource.setLoadBalancingPolicy(robinPolicy);

        cacheStoreFactory.setDataSource(dataSource);

        String persistenceSettingsXml = appConfig.getProperty(IGNITE_PERSISTENCE_SETTINGS);
        KeyValuePersistenceSettings persistenceSettings = getPersistenceSettings(persistenceSettingsXml);
        cacheStoreFactory.setPersistenceSettings(persistenceSettings);
        cacheConfig.setCacheStoreFactory(cacheStoreFactory);

        cacheConfig.setWriteThrough(true);
//        cacheConfig.setWriteBehindEnabled(true);
        cacheConfig.setReadThrough(true);

        // Sets the cache configuration
        igniteConfig.setCacheConfiguration(cacheConfig);

        // Starting Ignite
        Ignite ignite = Ignition.start(igniteConfig);
    }

    public static KeyValuePersistenceSettings getPersistenceSettings(String filename) throws URISyntaxException, IOException {
        Path path = Path.of(".", filename);
        String xmlLine = Files.readString(path, StandardCharsets.UTF_8);
        logger.debug(xmlLine);
        KeyValuePersistenceSettings settings = new KeyValuePersistenceSettings(xmlLine);
        return settings;
    }
}
