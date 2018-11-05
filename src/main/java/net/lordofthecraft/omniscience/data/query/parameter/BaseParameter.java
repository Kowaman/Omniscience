package net.lordofthecraft.omniscience.data.query.parameter;

import org.bson.Document;

public abstract class BaseParameter implements QueryParameter {

    protected abstract void addAdditionalQueriesToDocument(Document document);

    @Override
    public Document getQueryDocument() {
        Document document = new Document();
        addAdditionalQueriesToDocument(document);
        return document;
    }
}
