package net.lordofthecraft.omniscience.interfaces;

import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.query.Query;
import org.bukkit.command.CommandSender;

public interface IOmniscience {

    Class<? extends DataEntry> getEventClass(String name);

    void submitQuery(CommandSender sender, Query query);
}
