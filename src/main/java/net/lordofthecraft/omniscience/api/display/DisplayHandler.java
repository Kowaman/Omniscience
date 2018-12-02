package net.lordofthecraft.omniscience.api.display;

import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.query.QuerySession;

import java.util.List;
import java.util.Optional;

public interface DisplayHandler {

    boolean handles(String displayTag);

    Optional<String> buildTargetMessage(DataEntry entry, String target, QuerySession session);

    Optional<List<String>> buildAdditionalHoverData(DataEntry entry, QuerySession session);
}
