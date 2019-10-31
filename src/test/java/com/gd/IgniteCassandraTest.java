package com.gd;

import org.apache.ignite.cache.store.cassandra.persistence.KeyValuePersistenceSettings;
import org.junit.Test;

import static org.junit.Assert.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.util.Properties;

public class IgniteCassandraTest {

    final static Logger logger = LogManager.getLogger(IgniteCassandra.class);

    @Test
    public void testPropertiesLoad() {
        String testResource = "ignite-cassandra-test.properties";
        Properties props = IgniteCassandra.getConfiguration(testResource);
        assertFalse(props.isEmpty());
    }

    @Test
    public void testPersistenceSettingsLoad() throws URISyntaxException {
        String testResource = "persistence-settings-test.xml";
        KeyValuePersistenceSettings settings = IgniteCassandra.getPersistenceSettings(testResource);
        assertEquals(settings.getTable(), "access_log_test");
        assertEquals(settings.getKeyspace(), "ignite");
    }

    @Test
    public void testPersistenceSettingsLoad2() throws URISyntaxException {
        String testResource = "persistence-settings.xml";
        KeyValuePersistenceSettings settings = IgniteCassandra.getPersistenceSettings(testResource);
    }

}
