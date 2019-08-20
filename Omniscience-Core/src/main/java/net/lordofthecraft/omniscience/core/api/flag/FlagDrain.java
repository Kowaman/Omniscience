package net.lordofthecraft.omniscience.core.api.flag;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.flag.BaseFlagHandler;
import net.lordofthecraft.omniscience.api.flag.Flag;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FlagDrain extends BaseFlagHandler {

    public FlagDrain() {
        super(ImmutableList.of("drain"));
    }

    @Override
    public boolean acceptsSource(CommandSender sender) {
        return true;
    }

    @Override
    public boolean acceptsValue(String value) {
        return true;
    }

    @Override
    public Optional<CompletableFuture<?>> process(QuerySession session, String flag, String value, Query query) {
        session.addFlag(Flag.DRAIN);
        return Optional.empty();
    }
}
