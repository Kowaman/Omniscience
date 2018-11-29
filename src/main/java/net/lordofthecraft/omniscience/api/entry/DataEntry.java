package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.data.DataWrapper;

import java.lang.reflect.InvocationTargetException;

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
            case "join":
                return "joined";
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
}
