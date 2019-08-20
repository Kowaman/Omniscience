package net.lordofthecraft.omniscience.core.api.entry.entrybuilder;


import net.lordofthecraft.omniscience.api.OmniApi;
import net.lordofthecraft.omniscience.api.data.DataKey;
import net.lordofthecraft.omniscience.api.entry.EntryQueue;
import net.lordofthecraft.omniscience.api.entry.entrybuilder.EntryBuilder;
import net.lordofthecraft.omniscience.api.entry.entrybuilder.EventBuilder;
import net.lordofthecraft.omniscience.api.entry.entrybuilder.SourceBuilder;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Date;

import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

public class BukkitEntryBuilder extends EntryBuilder {

    public BukkitEntryBuilder(SourceBuilder sourceBuilder, EventBuilder eventBuilder) {
        super(sourceBuilder, eventBuilder);
    }

    @Override
    public void save() {
        if (!OmniApi.isEventRegistered(eventBuilder.getEventName())) {
            throw new IllegalArgumentException(eventBuilder.getEventName() + " is not registered with Omniscience. This must be done to continue.");
        }
        eventBuilder.getWrapper().set(EVENT_NAME, eventBuilder.getEventName());
        eventBuilder.getWrapper().set(CREATED, new Date());

        DataKey cause = (sourceBuilder.getSource() instanceof Player) ? PLAYER_ID : CAUSE;

        String causeId = "environment";
        if (sourceBuilder.getSource() instanceof Player) {
            causeId = ((Player) sourceBuilder.getSource()).getUniqueId().toString();
        } else if (sourceBuilder.getSource() instanceof Entity) {
            causeId = ((Entity) sourceBuilder.getSource()).getType().name();
        } else if (sourceBuilder.getSource() instanceof Plugin) {
            causeId = "pl@" + ((Plugin) sourceBuilder.getSource()).getName().replace(' ', '_');
        } else if (sourceBuilder.getSource() instanceof ConsoleCommandSender) {
            causeId = "console";
        } else if (sourceBuilder.getSource() instanceof RemoteConsoleCommandSender) {
            causeId = "remote_console";
        } else if (sourceBuilder.getSource() instanceof BlockCommandSender) {
            BlockCommandSender sender = (BlockCommandSender) sourceBuilder.getSource();
            CommandBlock commandBlock = (CommandBlock) sender.getBlock().getState();
            eventBuilder.getWrapper().set(X, commandBlock.getX());
            eventBuilder.getWrapper().set(Y, commandBlock.getY());
            eventBuilder.getWrapper().set(Z, commandBlock.getZ());
            eventBuilder.getWrapper().set(WORLD, commandBlock.getWorld().getUID().toString());
            causeId = "command_block";
            if (commandBlock.getName() != null) {
                causeId = causeId + " (" + commandBlock.getName() + ")";
            }
        }

        eventBuilder.getWrapper().set(cause, causeId);

        EntryQueue.submit(eventBuilder.getWrapper());
    }
}
