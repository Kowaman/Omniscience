package net.lordofthecraft.omniscience.interfaces;

import net.lordofthecraft.omniscience.api.entry.DataEntry;

import java.util.Optional;

public interface IOmniscience {

    Optional<Class<? extends DataEntry>> getEventClass(String name);
}
