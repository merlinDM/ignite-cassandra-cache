package com.gd;

import com.gd.model.Ipfix;
import com.gd.model.IpfixKey;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.store.cassandra.persistence.KeyValuePersistenceSettings;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.util.Date;

import static org.junit.Assert.*;

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

    @Test
    public void testIpfixModel() {

        String pathToProperties = FileSystems
                .getDefault()
                .getPath("build", "resources", "test", "ignite-cassandra-test.properties")
                .toAbsolutePath()
                .toString();
        System.out.println(pathToProperties);

        IgniteConfiguration igniteConfig = null;
        try {
            igniteConfig = IgniteCassandra.setupIgniteConfiguration("ignite-cassandra-test.properties");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            fail();
        }

        Ignite ignite = Ignition.start(igniteConfig);

        IgniteCache<IpfixKey, Ipfix> cache = ignite.getOrCreateCache("access_log_test");

        Ipfix value = new Ipfix();
        value.setIp("10.10.1.10");
        value.setUrl("google.com");
        value.setEventTime(new Date());
        value.setEventType("click");
        IpfixKey key = new IpfixKey(value);
        cache.put(key, value);

        Ipfix result = cache.get(key);

        assertTrue(result.equals(value));
    }
}
