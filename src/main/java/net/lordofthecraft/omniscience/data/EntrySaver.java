package net.lordofthecraft.omniscience.data;

import net.lordofthecraft.omniscience.domain.DataEntry;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bson.Document;

public class EntrySaver {

    private final MongoConnectionHandler connectionHandler;

    public EntrySaver(MongoConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public void saveDataEntry(DataEntry entry) {
        Document document = entry.asDocument();
        connectionHandler.getDataCollection().insertOne(document, (result, t) -> {

        });
    }
}
