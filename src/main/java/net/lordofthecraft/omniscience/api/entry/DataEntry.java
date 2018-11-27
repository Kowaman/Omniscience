package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.data.DataWrapper;

import java.lang.reflect.InvocationTargetException;

public abstract class DataEntry {

    protected DataWrapper data;

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
