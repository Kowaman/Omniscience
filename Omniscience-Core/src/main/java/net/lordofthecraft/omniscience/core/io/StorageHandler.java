package net.lordofthecraft.omniscience.core.io;

import net.lordofthecraft.omniscience.core.Omniscience;

public interface StorageHandler {

    boolean connect(Omniscience omniscience) throws Exception;

    RecordHandler records();

    void close();
}
