package net.lordofthecraft.omniscience.mongo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.connection.ClusterSettings;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.entry.EntryMapper;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MongoConnectionHandler {

    private static MongoConnectionHandler instance;

    public static MongoConnectionHandler getInstance() {
        return instance;
    }

    private final MongoClient client;
    private final MongoDatabase database;
    private MongoCollection<Document> dataEntryCollection = null;

    private MongoConnectionHandler(MongoClient client, MongoDatabase database) {
        this.client = client;
        this.database = database;
    }

    public static MongoConnectionHandler createHandler(FileConfiguration configuration) {
        if (instance != null) {
            return instance;
        }
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
        MongoConnectionHandler handler = new MongoConnectionHandler(client, database);
        instance = handler;
        return handler;
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    private MongoCollection<Document> getDataCollection() {
        if (dataEntryCollection == null) {
            database.getCollection("DataEntry");
        }
        return dataEntryCollection;
    }

    public CompletableFuture<List<DataEntry>> query(QuerySession session) {
        Query query = session.getQuery();
        checkNotNull(query);

        List<DataEntry> entries = Lists.newArrayList();
        CompletableFuture<List<DataEntry>> future = new CompletableFuture<>();

        MongoCollection<Document> collection = getDataCollection();

        final AggregateIterable<Document> aggregated = null;


        try (MongoCursor<Document> cursor = aggregated.iterator()) {
            while (cursor.hasNext()) {
                Document wrapper = cursor.next();
                Document document = wrapper;

                Optional<DataEntry> entry = EntryMapper.INSTANCE.mapDocumentToDataEntry(document);

                entry.ifPresent(entries::add);
            }
            future.complete(entries);
        }
        return future;
    }
}
