package net.lordofthecraft.omniscience.core.io;

import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.query.QuerySession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RecordHandler {

    void write(List<DataWrapper> wrappers);

    CompletableFuture<List<DataEntry>> query(QuerySession session) throws Exception;

}
