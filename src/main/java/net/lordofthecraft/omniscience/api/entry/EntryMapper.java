package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public enum EntryMapper {
    INSTANCE;

    private MongoConnectionHandler connectionHandler;

    public Optional<DataEntry> mapDocumentToDataEntry(Document document) {
        String event = document.getString("event");
        Class<? extends DataEntry> clazz = Omniscience.getInstance().getEventClass(event);
        if (clazz != null) {
            try {
                DataEntry emptyEntry = clazz.getConstructor().newInstance();
                emptyEntry.loadFromDocument(document);
                return Optional.of(emptyEntry);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
