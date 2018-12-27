package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.data.DataWrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

public abstract class DataEntry {

    public DataWrapper data;

    public String getVerbPastTense() {
        return translateToPastTense(getEventName());
    }

    public String getEventName() {
        return data.getString(EVENT_NAME).orElse("unknown");
    }

    public String getSourceName() {
        return data.getString(CAUSE).orElse("unknown");
    }

    public String getTargetName() {
        return data.getString(TARGET).orElse("");
    }

    private String translateToPastTense(String word) {
        switch (word) {
            case "break":
                return "broke";
            case "decay":
                return "decayed";
            case "burn":
                return "burned";
            case "grow":
                return "grown";
            case "form":
                return "formed";
            case "place":
                return "placed";
            case "death":
                return "killed";
            case "drop":
                return "dropped";
            case "pickup":
                return "picked up";
            case "say":
                return "said";
            case "command":
                return "ran command";
            case "useSign":
                return "used sign";
            case "quit":
                return "quit from";
            case "join":
                return "joined from";
            case "withdraw":
                return "withdrew";
            case "deposit":
                return "deposited";
            case "open":
                return "opened";
            case "close":
                return "closed";
            default:
                return word;
        }
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

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataEntry)) return false;
        DataEntry dataEntry = (DataEntry) o;
        return Objects.equals(data, dataEntry.data);
    }

    @Override
    public String toString() {
        return "DataEntry{" +
                "data=" + data +
                '}';
    }
}
