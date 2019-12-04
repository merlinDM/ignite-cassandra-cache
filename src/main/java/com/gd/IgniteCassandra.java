package com.gd;

import com.datastax.driver.core.policies.RoundRobinPolicy;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.store.cassandra.CassandraCacheStoreFactory;
import org.apache.ignite.cache.store.cassandra.datasource.DataSource;
import org.apache.ignite.cache.store.cassandra.persistence.KeyValuePersistenceSettings;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class IgniteCassandra {

    private static Logger logger = LogManager.getLogger(IgniteCassandra.class.getName());
    private static final String APP_PROPERTIES_FILE = "ignite-cassandra.properties";
    private static final String CASSANDRA_CONTACT_POINTS = "cassandra.contactPoints";
    private static final String CASSANDRA_USER_NAME = "cassandra.username";
    private static final String CASSANDRA_USER_PASSWORD = "cassandra.password";
    private static final String IGNITE_PERSISTENCE_SETTINGS = "ignite.persistenceSettings";
    public  static final String CACHE_NAME = "ignite-cassandra";
    private static final String LOG4J_PROPERTIES = "log4j.properties";

    public static void main(String[] args) throws IOException, URISyntaxException {
        IgniteConfiguration igniteConfig = setupIgniteConfiguration(IgniteCassandra.APP_PROPERTIES_FILE);

        Ignite ignite = Ignition.start(igniteConfig);
    }

    @NotNull
    public static IgniteConfiguration setupIgniteConfiguration(String appPropertiesFile) throws URISyntaxException, IOException {
        PropertiesLogger appConfig = new PropertiesLogger();
        appConfig.load(appPropertiesFile);

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

        return igniteConfig;
    }

    @NotNull
    public static KeyValuePersistenceSettings getPersistenceSettings(String filename) throws URISyntaxException, IOException {
        String xmlLine = null;
        try {
            byte[] bytes = IgniteCassandra
                    .class
                    .getClassLoader()
                    .getResourceAsStream(filename)
                    .readAllBytes();
            xmlLine = new String(bytes);
        }
        catch (NullPointerException npe) {
            logger.info(String.format("Couldn't find %s in resoures.", filename));
        }
        if (xmlLine == null) {
            logger.info(String.format("Assuming %s is absolute path", filename));
            Path path = FileSystems
                    .getDefault()
                    .getPath(filename)
                    .toAbsolutePath();
            xmlLine = Files.readString(path, StandardCharsets.UTF_8);
        }
        logger.debug(xmlLine);
        KeyValuePersistenceSettings settings = new KeyValuePersistenceSettings(xmlLine);
        return settings;
    }
}
