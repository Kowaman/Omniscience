package net.lordofthecraft.omniscience.io.mongo;

import com.google.common.collect.Maps;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.connection.ClusterSettings;
import net.lordofthecraft.omniscience.OmniConfig;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.io.RecordHandler;
import net.lordofthecraft.omniscience.io.StorageHandler;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MongoStorageHandler implements StorageHandler {

    private static MongoDatabase database;
    private final String collectionName;
    private MongoRecordHandler recordHandler;

    public MongoStorageHandler() {
        this.collectionName = OmniConfig.INSTANCE.getTableName();
    }

    protected static MongoCollection<Document> getCollection(String collectionName) {
        try {
            return database.getCollection(collectionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean connect(Omniscience omniscience) {
        Map<?, ?> serverList = omniscience.getConfig().getMapList("mongodb.servers").get(0);
        Map<ServerAddress, MongoCredential> addressMongoCredentialMap = Maps.newHashMap();
        for (Map.Entry<?, ?> server : serverList.entrySet()) {
            String serverName = (String) server.getKey();
            Map<String, Object> serverProperties = (Map<String, Object>) server.getValue();
            String host = (String) serverProperties.get("address");
            int port = (int) serverProperties.get("port");
            boolean auth = (boolean) serverProperties.get("usesauth");
            String username = (String) serverProperties.get("user");
            String password = (String) serverProperties.get("pass");
            addressMongoCredentialMap.put(new ServerAddress(host, port), auth ? MongoCredential.createCredential(username, host, password.toCharArray()) : null);
        }

        ClusterSettings clusterSettings = ClusterSettings
                .builder()
                .hosts(new ArrayList<>(addressMongoCredentialMap.keySet()))
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.applySettings(clusterSettings))
                .build();
        MongoClient client = MongoClients.create(settings);
        database = client.getDatabase(OmniConfig.INSTANCE.getDatabaseName());
        this.recordHandler = new MongoRecordHandler(this);
        try {
            getCollection(collectionName).createIndex(
                    new Document("Location.X", 1).append("Location.Z", 1).append("Location.Y", 1).append("Created", -1)
            );
            getCollection(collectionName).createIndex(new Document("Created", -1).append("EventName", 1));

            IndexOptions options = new IndexOptions().expireAfter(0L, TimeUnit.SECONDS);
            getCollection(collectionName).createIndex(new Document("Expires", 1), options);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public RecordHandler records() {
        return recordHandler;
    }

    @Override
    public void close() {

    }
}
