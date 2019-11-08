package com.gd;

import org.apache.ignite.cache.store.cassandra.persistence.KeyValuePersistenceSettings;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class IgniteCassandraTest {

    final static Logger logger = LogManager.getLogger(IgniteCassandra.class);

    @Test
    public void testPropertiesLoad() {
        String testResource = "ignite-cassandra-test.properties";
        PropertiesLogger props = new PropertiesLogger();
        props.load(testResource);
        assertFalse(props.isEmpty());
    }

    @Test
    public void testPersistenceSettingsLoad() throws URISyntaxException, IOException {
        String testResource = "src/test/resources/persistence-settings-test.xml";
        KeyValuePersistenceSettings settings = IgniteCassandra.getPersistenceSettings(testResource);
        assertEquals(settings.getTable(), "access_log_test");
        assertEquals(settings.getKeyspace(), "ignite");
    }

//    @Test
//    public void testPersistenceSettingsLoad2() throws URISyntaxException, IOException {
//        String testResource = "/src/test/resources/persistence-settings.xml";
//        KeyValuePersistenceSettings settings = IgniteCassandra.getPersistenceSettings(testResource);
//    }

}
