package net.lordofthecraft.omniscience.interfaces;

import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.query.Query;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public interface IOmniscience {

    Optional<Class<? extends DataEntry>> getEventClass(String name);

    void submitQuery(CommandSender sender, Query query);
}
