package net.lordofthecraft.omniscience.core.api.flag;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.flag.BaseFlagHandler;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.core.Omniscience;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FlagGlobal extends BaseFlagHandler {

    public FlagGlobal() {
        super(ImmutableList.of("g"));
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
        Omniscience.getParameterHandler("r").ifPresent(session::addIgnoredDefault);
        return Optional.empty();
    }
}
