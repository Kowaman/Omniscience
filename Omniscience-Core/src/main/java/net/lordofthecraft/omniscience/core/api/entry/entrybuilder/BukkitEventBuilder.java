package net.lordofthecraft.omniscience.core.api.entry.entrybuilder;

import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.api.entry.entrybuilder.EntryBuilder;
import net.lordofthecraft.omniscience.api.entry.entrybuilder.EventBuilder;
import net.lordofthecraft.omniscience.api.entry.entrybuilder.SourceBuilder;

public class BukkitEventBuilder implements EventBuilder {

    private final SourceBuilder sourceBuilder;
    private String eventName;
    private DataWrapper wrapper = DataWrapper.createNew();

    public BukkitEventBuilder(SourceBuilder sourceBuilder) {
        this.sourceBuilder = sourceBuilder;
    }

    @Override
    public DataWrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public SourceBuilder getSource() {
        return sourceBuilder;
    }

    @Override
    public EntryBuilder withData(String eventName, DataWrapper wrapperData) {
        this.eventName = eventName;
        wrapperData.getKeys(false).forEach(key -> {
            wrapperData.get(key).ifPresent(data -> wrapper.set(key, data));
        });
        return new BukkitEntryBuilder(sourceBuilder, this);
    }
}
