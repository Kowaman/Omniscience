package net.lordofthecraft.omniscience.api.query;

import org.bson.Document;

public class QueryParameter {


    QueryParameter merge(QueryParameter other) {

        return this;
    }

    boolean isMergable(QueryParameter other) {

        return false;
    }

    void modifyDocument(Document queryDocument) {

    }
}
