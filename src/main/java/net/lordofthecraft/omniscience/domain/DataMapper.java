package net.lordofthecraft.omniscience.domain;

import com.google.common.collect.Maps;
import net.lordofthecraft.omniscience.domain.block.BlockEntry;
import org.bson.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public enum DataMapper {
    INSTANCE;

    private Map<String, Class<? extends DataEntry>> dataMapping;

    public void initInternal() {
        if (dataMapping != null) {
            throw new IllegalStateException("initInternal called twice!");
        }
        dataMapping = Maps.newHashMap();
        dataMapping.put("BLOCK", BlockEntry.class);
    }

    public void register(String identifier, Class<? extends DataEntry> clazz) {
        if (!dataMapping.containsKey(identifier)) {
            dataMapping.put(identifier, clazz);
        }
    }

    public DataEntry mapToEntry(Document document) {
        String type = document.getString("type");
        if (dataMapping.containsKey(type)) {
            Class<? extends DataEntry> clazz = dataMapping.get(type);
            try {
                Constructor<? extends DataEntry> constructor = clazz.getConstructor(int.class, int.class, int.class, UUID.class, Actor.class, Date.class);
                DataEntry entry = constructor.newInstance(document.getInteger("x"),
                        document.getInteger("y"),
                        document.getInteger("z"),
                        UUID.fromString(document.getString("world")),
                        (Actor) null,
                        document.getDate("date"));
                entry.fromDocument(document);
                return entry;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
