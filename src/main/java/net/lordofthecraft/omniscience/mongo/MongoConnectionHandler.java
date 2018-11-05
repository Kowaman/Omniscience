package net.lordofthecraft.omniscience.mongo;

import com.google.common.collect.Maps;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Map;

public final class MongoConnectionHandler {

    private final MongoClient client;
    private final MongoDatabase database;
    private MongoCollection<Document> dataEntryCollection = null;

    private MongoConnectionHandler(MongoClient client, MongoDatabase database) {
        this.client = client;
        this.database = database;
    }

    public static MongoConnectionHandler createHandler(FileConfiguration configuration) {
        Map<?, ?> serverList = configuration.getMapList("mongodb.servers").get(0);
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
        MongoDatabase database = client.getDatabase("Omniscience");
        return new MongoConnectionHandler(client, database);
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getDataCollection() {
        if (dataEntryCollection == null) {
            database.getCollection("DataEntry");
        }
        return dataEntryCollection;
    }
}
