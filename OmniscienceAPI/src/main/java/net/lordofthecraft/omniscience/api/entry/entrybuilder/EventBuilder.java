package net.lordofthecraft.omniscience.api.entry.entrybuilder;

import net.lordofthecraft.omniscience.api.data.DataWrapper;

public interface EventBuilder {

    DataWrapper getWrapper();

    String getEventName();

    SourceBuilder getSource();

    EntryBuilder withData(String eventName, DataWrapper wrapperData);

}
