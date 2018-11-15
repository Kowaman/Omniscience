package net.lordofthecraft.omniscience.api.query;

import com.google.common.collect.Lists;
import org.bson.Document;

import java.util.List;

public class Query {

    private List<QueryParameter> searchCriteria;

    public Query() {
        this.searchCriteria = Lists.newArrayList();
    }

    public void addParameter(QueryParameter parameter) {
        for (QueryParameter param : searchCriteria) {
            if (parameter.getClass().isInstance(param)) {

            }
        }
        searchCriteria.add(parameter);
    }

    /**
     * Builds a document that provides various search criteria options
     *
     * @return A document that will be used for search queries
     */
    Document build() {
        Document document = new Document();
        searchCriteria.forEach(param -> param.modifyDocument(document));
        return document;
    }
}
