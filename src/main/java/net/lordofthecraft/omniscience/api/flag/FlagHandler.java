package net.lordofthecraft.omniscience.api.flag;

import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface FlagHandler {

    boolean acceptsSource(CommandSender sender);

    boolean acceptsValue(String value);

    boolean handles(String flag);

    Optional<CompletableFuture<?>> process(QuerySession session, String flag, String value, Query query);
}
