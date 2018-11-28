package net.lordofthecraft.omniscience.command.async;

import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.api.entry.DataAggregateEntry;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.entry.DataEntryComplete;
import net.lordofthecraft.omniscience.api.flag.Flag;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.command.commands.PageCommand;
import net.lordofthecraft.omniscience.util.DataHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.List;
import java.util.stream.Collectors;

public class SearchCallback implements AsyncCallback {

    private final QuerySession session;

    public SearchCallback(QuerySession session) {
        this.session = session;
    }

    @Override
    public void success(List<DataEntry> results) {
        List<BaseComponent[]> components = results.stream().map(this::buildComponent).collect(Collectors.toList());
        PageCommand.setSearchResults(session.getSender(), components);
    }

    @Override
    public void empty() {
        session.getSender().sendMessage(ChatColor.RED + "Nothing was found. Check /omni help for help.");
    }

    @Override
    public void error(Exception e) {
        session.getSender().sendMessage(ChatColor.RED + "An error occurred. Please see console.");
        //TODO log the error
    }

    private BaseComponent[] buildComponent(DataEntry entry) {
        ComponentBuilder builder = new ComponentBuilder("");
        builder.append(entry.getSourceName()).color(ChatColor.DARK_AQUA).append(" ");
        builder.append(entry.getVerbPastTense()).color(ChatColor.WHITE).append(" ");

        //TODO this would be FUCKING AWESOME to show the item!
        ComponentBuilder hoverBuilder = new ComponentBuilder("");
        hoverBuilder.append("Source: ").color(ChatColor.DARK_GRAY).append(entry.getSourceName()).color(ChatColor.WHITE).append("\n");
        hoverBuilder.append("Event: ").color(ChatColor.DARK_GRAY).append(entry.getEventName()).color(ChatColor.WHITE).append("\n");

        String quantity = entry.data.getString(DataKeys.QUANTITY).orElse(null);
        if (quantity != null && !quantity.isEmpty()) {
            builder.append(quantity).color(ChatColor.DARK_AQUA).append(" ");
            hoverBuilder.append("Quantity: ").color(ChatColor.DARK_GRAY).append(quantity).color(ChatColor.WHITE).append("\n");
        }

        String target = entry.data.getString(DataKeys.TARGET).orElse("Unknown");
        if (!target.isEmpty()) {
            builder.append(target).color(ChatColor.DARK_AQUA).append(" ");
            hoverBuilder.append("Target: ").color(ChatColor.DARK_AQUA).append(target).color(ChatColor.WHITE).append("\n");
        }

        if (entry instanceof DataAggregateEntry) {
            entry.data.getInt(DataKeys.COUNT).ifPresent(count -> {
                builder.append("x" + count).color(ChatColor.GREEN).append(" ");
                hoverBuilder.append("Count: ").color(ChatColor.DARK_GRAY).append(String.valueOf(count)).color(ChatColor.WHITE).append("\n");
            });
        }

        if (entry instanceof DataEntryComplete) {
            DataEntryComplete complete = (DataEntryComplete) entry;

            builder.append(complete.getRelativeTime()).color(ChatColor.WHITE);
            hoverBuilder.append("Time: ").color(ChatColor.DARK_GRAY).append(complete.getRelativeTime()).append("\n");

            complete.data.get(DataKeys.LOCATION).ifPresent(oLoc -> {
                DataWrapper wrapper = (DataWrapper) oLoc;
                DataHelper.getLocationFromDataWrapper(wrapper).ifPresent(location -> {
                    if (this.session.hasFlag(Flag.EXTENDED)) {
                        builder.append("\n").append(" - ").color(ChatColor.GRAY).append(DataHelper.buildLocation(location, true)).color(ChatColor.GRAY);
                    }

                    hoverBuilder.append("Location: ").color(ChatColor.DARK_GRAY).append(DataHelper.buildLocation(location, false)).color(ChatColor.WHITE);
                });
            });
        }

        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.create()));
        return builder.create();
    }
}
