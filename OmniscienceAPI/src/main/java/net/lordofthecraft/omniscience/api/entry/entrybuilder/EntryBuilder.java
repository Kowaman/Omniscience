package net.lordofthecraft.omniscience.api.entry.entrybuilder;

public abstract class EntryBuilder {

    protected SourceBuilder sourceBuilder;
    protected EventBuilder eventBuilder;

    public EntryBuilder(SourceBuilder sourceBuilder, EventBuilder eventBuilder) {
        this.sourceBuilder = sourceBuilder;
        this.eventBuilder = eventBuilder;
    }

    public SourceBuilder getSourceBuilder() {
        return sourceBuilder;
    }

    public EventBuilder getEventBuilder() {
        return eventBuilder;
    }

    public abstract void save();

}
