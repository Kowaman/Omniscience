package net.lordofthecraft.omniscience.api.query;

import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;
import org.bson.Document;

public class QueryParameter {

    public static QueryParameter fromHandler(ParameterHandler handler) {

        return null;
    }


    QueryParameter merge(QueryParameter other) {

        return this;
    }

    boolean isMergable(QueryParameter other) {

        return false;
    }

    void modifyDocument(Document queryDocument) {

    }
}
