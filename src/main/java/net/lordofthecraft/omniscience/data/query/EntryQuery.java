package net.lordofthecraft.omniscience.data.query;

import com.google.common.collect.Lists;
import com.mongodb.Block;
import com.mongodb.async.client.MongoCollection;
import net.lordofthecraft.omniscience.data.query.parameter.QueryParameter;
import net.lordofthecraft.omniscience.domain.DataEntry;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class EntryQuery {

    private List<QueryParameter> queryParameters;
    private MongoConnectionHandler connectionHandler;

    private EntryQuery() {
    }

    public static EntryQuery.Builder Builder() {
        return new Builder();
    }

    private EntryQuery build(Builder builder, MongoConnectionHandler connectionHandler) {
        queryParameters = new ArrayList<>(builder.queryParameters);
        this.connectionHandler = connectionHandler;
        return this;
    }

    public List<DataEntry> run(Block<Document> cacher) {
        List<DataEntry> list = Lists.newArrayList();
        MongoCollection<Document> collection = connectionHandler.getDataCollection();
        //collection.find(and(queryParameters.stream().map(QueryParameter::buildBsonFilters).collect(Collectors.toSet())));
        return list;
    }

    public static class Builder {
        private List<QueryParameter> queryParameters = Lists.newArrayList();

        private Builder() {
        }

        public Builder addParameter(QueryParameter parameter) {
            this.queryParameters.add(parameter);
            return this;
        }

        public EntryQuery build(MongoConnectionHandler connectionHandler) {
            return new EntryQuery().build(this, connectionHandler);
        }
    }
}
