package net.lordofthecraft.omniscience.mongo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.connection.ClusterSettings;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.entry.EntryMapper;
import net.lordofthecraft.omniscience.api.query.*;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.lordofthecraft.omniscience.api.query.DataKeys.*;

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

    private Document buildConditions(List<SearchCondition> conditions) {
        Document filter = new Document();

        for (SearchCondition condition : conditions) {
            if (condition instanceof SearchConditionGroup) {
                SearchConditionGroup group = (SearchConditionGroup) condition;
                Document subFilter = buildConditions(group.getConditions());
                if (group.getOperator().equals(SearchConditionGroup.Operator.OR)) {
                    filter.append("$or", subFilter);
                } else {
                    filter.putAll(subFilter);
                }
            } else {
                FieldCondition field = (FieldCondition) condition;

                Document matcher;
                if (filter.containsKey(field.getField())) {
                    matcher = (Document) filter.get(field.getField());
                } else {
                    matcher = new Document();
                }

                if (field.getValue() instanceof List) {
                    matcher.append(field.getRule().equals(MatchRule.INCLUDES) ? "$in" : "$nin", field.getValue());
                    filter.put(field.getField(), matcher);
                } else if (field.getRule().equals(MatchRule.EQUALS)) {
                    filter.put(field.getField(), field.getValue());
                } else if (field.getRule().equals(MatchRule.GREATER_THAN_EQUAL)) {
                    matcher.append("$gte", field.getValue());
                    filter.put(field.getField(), matcher);
                } else if (field.getRule().equals(MatchRule.LESS_THAN_EQUAL)) {
                    matcher.append("$lte", field.getValue());
                    filter.put(field.getField(), matcher);
                } else if (field.getRule().equals(MatchRule.BETWEEN)) {
                    if (!(field.getValue() instanceof Range)) {
                        throw new IllegalArgumentException("Between matcher requires a value range");
                    }

                    Range<?> range = (Range<?>) field.getValue();

                    Document between = new Document("$gte", range.lowerEndpoint()).append("$lte", range.upperEndpoint());
                    filter.put(field.getField(), between);
                }
            }
        }
        return filter;
    }

    public CompletableFuture<List<DataEntry>> query(QuerySession session) {
        Query query = session.getQuery();
        checkNotNull(query);

        List<DataEntry> entries = Lists.newArrayList();
        CompletableFuture<List<DataEntry>> future = new CompletableFuture<>();

        MongoCollection<Document> collection = getDataCollection();

        Document matcher = new Document("$match", buildConditions(session.getQuery().getSearchCriteria()));

        Document sortFields = new Document();
        sortFields.put(CREATED, "value");
        Document sorter = new Document("$sort", sortFields);

        Document limit = new Document("$limit", 10);

        final AggregateIterable<Document> aggregated;
        if (session != null) { //It's a me, mario. please replace.
            Document groupFields = new Document();
            groupFields.put(EVENT_NAME, "$" + EVENT_NAME);
            groupFields.put(PLAYER_ID, "$" + PLAYER_ID);
            groupFields.put(CAUSE, "$" + CAUSE);
            groupFields.put(TARGET, "$" + TARGET);
            groupFields.put(DAY_OF_MONTH, "$" + CREATED);
            groupFields.put(MONTH, "$" + CREATED);
            groupFields.put(YEAR, "$" + CREATED);

            Document groupHolder = new Document("_id", groupFields);
            groupHolder.put(COUNT, new Document("$sum", 1));

            Document group = new Document("$group", groupHolder);

            List<Document> pipeline = Lists.newArrayList();
            pipeline.add(matcher);
            pipeline.add(group);
            pipeline.add(sorter);
            pipeline.add(limit);

            aggregated = collection.aggregate(pipeline);
            //TODO log
        } else {
            List<Document> pipeline = Lists.newArrayList();
            pipeline.add(matcher);
            pipeline.add(sorter);
            pipeline.add(limit);

            aggregated = collection.aggregate(pipeline);
        }


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
