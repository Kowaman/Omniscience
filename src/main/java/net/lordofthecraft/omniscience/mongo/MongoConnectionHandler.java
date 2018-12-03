package net.lordofthecraft.omniscience.mongo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.connection.ClusterSettings;
import net.lordofthecraft.omniscience.OmniConfig;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.data.DataKey;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.flag.Flag;
import net.lordofthecraft.omniscience.api.query.*;
import net.lordofthecraft.omniscience.util.DataHelper;
import net.lordofthecraft.omniscience.util.DateUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

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
            dataEntryCollection = database.getCollection("DataEntry");
        }
        return dataEntryCollection;
    }

    public void write(List<DataWrapper> wrappers) {
        MongoCollection<Document> collection = getDataCollection();

        List<WriteModel<Document>> documents = Lists.newArrayList();
        for (DataWrapper wrapper : wrappers) {
            Document document = documentFromDataWrapper(wrapper);

            document.append("Expires", DateUtil.parseTimeStringToDate(OmniConfig.INSTANCE.getRecordExpiry(), true));

            documents.add(new InsertOneModel<>(document));
        }

        collection.bulkWrite(documents);

        //TODO return result
    }

    private Document documentFromDataWrapper(DataWrapper wrapper) {
        Document document = new Document();

        Set<DataKey> keys = wrapper.getKeys(false);
        for (DataKey dataKey : keys) {
            Optional<Object> oObject = wrapper.get(dataKey);
            oObject.ifPresent(object -> {
                String key = dataKey.toString();
                if (object instanceof List) {
                    List<Object> convertedList = Lists.newArrayList();
                    for (Object innerObject : (List<?>) object) {
                        if (innerObject instanceof DataWrapper) {
                            convertedList.add(documentFromDataWrapper((DataWrapper) innerObject));
                        } else if (DataHelper.isPrimitiveType(innerObject)) {
                            convertedList.add(innerObject);
                        } else {
                            //TODO log unsupported data
                        }
                    }

                    if (!convertedList.isEmpty()) {
                        document.append(key, convertedList);
                    }
                } else if (object instanceof DataWrapper) {
                    DataWrapper subWrapper = (DataWrapper) object;
                    document.append(key, documentFromDataWrapper(subWrapper));
                } else {
                    if (key.equals(PLAYER_ID.toString())) {
                        document.append(PLAYER_ID.toString(), object);
                    } else {
                        document.append(key, object);
                    }
                }
            });
        }
        return document;
    }

    private DataWrapper documentToDataWrapper(Document document) {
        DataWrapper wrapper = DataWrapper.createNew();

        for (String key : document.keySet()) {
            DataKey dataKey = DataKey.of(key);
            Object object = document.get(key);

            if (object instanceof Document) {
                wrapper.set(dataKey, documentToDataWrapper((Document) object));
            } else {
                wrapper.set(dataKey, object);
            }
        }
        return wrapper;
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
                if (filter.containsKey(field.getField().toString())) {
                    matcher = (Document) filter.get(field.getField().toString());
                } else {
                    matcher = new Document();
                }

                if (field.getValue() instanceof List) {
                    matcher.append(field.getRule().equals(MatchRule.INCLUDES) ? "$in" : "$nin", field.getValue());
                    filter.put(field.getField().toString(), matcher);
                } else if (field.getRule().equals(MatchRule.EQUALS)) {
                    filter.put(field.getField().toString(), field.getValue());
                } else if (field.getRule().equals(MatchRule.GREATER_THAN_EQUAL)) {
                    matcher.append("$gte", field.getValue());
                    filter.put(field.getField().toString(), matcher);
                } else if (field.getRule().equals(MatchRule.LESS_THAN_EQUAL)) {
                    matcher.append("$lte", field.getValue());
                    filter.put(field.getField().toString(), matcher);
                } else if (field.getRule().equals(MatchRule.BETWEEN)) {
                    if (!(field.getValue() instanceof Range)) {
                        throw new IllegalArgumentException("Between matcher requires a value range");
                    }

                    Range<?> range = (Range<?>) field.getValue();

                    Document between = new Document("$gte", range.lowerEndpoint()).append("$lte", range.upperEndpoint());
                    filter.put(field.getField().toString(), between);
                }
            }
        }
        return filter;
    }

    public CompletableFuture<List<DataEntry>> query(QuerySession session) throws Exception {
        Query query = session.getQuery();
        checkNotNull(query);

        List<DataEntry> entries = Lists.newArrayList();
        CompletableFuture<List<DataEntry>> future = new CompletableFuture<>();

        MongoCollection<Document> collection = getDataCollection();

        Document matcher = new Document("$match", buildConditions(query.getSearchCriteria()));

        Document sortFields = new Document();
        sortFields.put(CREATED.toString(), session.getSortOrder().getSortVal());
        Document sorter = new Document("$sort", sortFields);

        Document limit = new Document("$limit", query.getSearchLimit());

        final AggregateIterable<Document> aggregated;
        if (!session.hasFlag(Flag.NO_GROUP)) {
            Document groupFields = new Document();
            groupFields.put(EVENT_NAME.toString(), "$" + EVENT_NAME);
            groupFields.put(PLAYER_ID.toString(), "$" + PLAYER_ID);
            groupFields.put(CAUSE.toString(), "$" + CAUSE);
            groupFields.put(TARGET.toString(), "$" + TARGET);

            //TODO group by entity type

            groupFields.put("dayOfMonth", new Document("$dayOfMonth", "$" + CREATED));
            groupFields.put("month", new Document("$month", "$" + CREATED));
            groupFields.put("year", new Document("$year", "$" + CREATED));

            Document groupHolder = new Document("_id", groupFields);
            groupHolder.put(COUNT.toString(), new Document("$sum", 1));

            Document group = new Document("$group", groupHolder);

            List<Document> pipeline = Lists.newArrayList();
            pipeline.add(matcher);
            pipeline.add(group);
            pipeline.add(sorter);
            pipeline.add(limit);

            aggregated = collection.aggregate(pipeline);
            Omniscience.getPluginInstance().getLogger().info("MongoDB Query: " + pipeline);
        } else {
            List<Document> pipeline = Lists.newArrayList();
            pipeline.add(matcher);
            pipeline.add(sorter);
            pipeline.add(limit);

            aggregated = collection.aggregate(pipeline);
            Omniscience.getPluginInstance().getLogger().info("MongoDB Query: " + pipeline);
        }

        try (MongoCursor<Document> cursor = aggregated.iterator()) {
            while (cursor.hasNext()) {
                Document wrapper = cursor.next();
                Document document = session.hasFlag(Flag.NO_GROUP) ? wrapper : (Document) wrapper.get("_id");

                DataWrapper internalWrapper = documentToDataWrapper(document);
                System.out.println("Loading data. Wrapper: " + wrapper);
                System.out.println("Document: " + document);
                System.out.println("internalWrapper: " + internalWrapper);

                if (!session.hasFlag(Flag.NO_GROUP)) {
                    internalWrapper.set(COUNT, wrapper.get(COUNT.toString()));
                }

                DataEntry entry = DataEntry.from(document.get(EVENT_NAME.toString()).toString(), !session.hasFlag(Flag.NO_GROUP));

                if (document.containsKey(PLAYER_ID.toString())) {
                    String uuid = document.getString(PLAYER_ID.toString());
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if (player != null) {
                        internalWrapper.set(CAUSE, player.getName());
                    } else {
                        internalWrapper.set(CAUSE, uuid);
                    }
                }

                entry.data = internalWrapper;
                entries.add(entry);
            }
            future.complete(entries);
        }
        return future;
    }
}
