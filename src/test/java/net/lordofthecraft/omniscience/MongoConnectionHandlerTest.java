package net.lordofthecraft.omniscience;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoDatabase;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class MongoConnectionHandlerTest {

    @Test
    public void testCreation() {
        MongoConnectionHandler connectionHandler = spy(makeMongoConnectionHandler(getDummyConfiguration()));
        assertEquals("Omniscience", connectionHandler.getDatabase().getName());
        MongoDatabase database = spy(connectionHandler.getDatabase());
        assertNotNull(connectionHandler.getClient());
        assertNotNull(connectionHandler.getDatabase());
    }

    private MongoConnectionHandler makeMongoConnectionHandler(FileConfiguration configuration) {
        return MongoConnectionHandler.createHandler(configuration);
    }

    private FileConfiguration getDummyConfiguration() {
        YamlConfiguration configuration = new YamlConfiguration();
        List<Map<String, Map<String, Object>>> values = Lists.newArrayList();
        Map<String, Object> deepVals = Maps.newHashMap();
        deepVals.put("address", "90.0.12.3");
        deepVals.put("port", 27017);
        deepVals.put("usesauth", false);
        deepVals.put("user", "username");
        deepVals.put("pass", "password");
        Map<String, Map<String, Object>> vals = Maps.newHashMap();
        vals.put("ServerA", deepVals);
        values.add(vals);
        configuration.set("mongodb.servers", values);
        return configuration;
    }
}
