package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.Omniscience;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public abstract class DataEntry {

    private String eventName;
    private int x, y, z;
    private UUID world;


    public void save() {

    }

    public static DataEntry from(String eventName, boolean isAggregate) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final DataEntry entry;
        if (isAggregate) {
            entry = new DataAggregateEntry();
        } else {
            entry = Omniscience.getDataEntryClass(eventName)
                    .orElse(DataEntryComplete.class).getConstructor().newInstance();
        }

        return entry;
    }
}
