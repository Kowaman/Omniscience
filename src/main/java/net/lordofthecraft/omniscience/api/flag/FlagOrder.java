package net.lordofthecraft.omniscience.api.flag;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FlagOrder extends BaseFlagHandler {

    public FlagOrder() {
        super(ImmutableList.of("ord", "order"));
    }

    @Override
    public boolean acceptsSource(CommandSender sender) {
        return true;
    }

    @Override
    public boolean acceptsValue(String value) {
        switch (value) {
            case "new":
            case "newest":
            case "desc":
            case "old":
            case "oldest":
            case "asc":
                return true;
            default:
                return false;
        }
    }

    @Override
    public Optional<CompletableFuture<?>> process(QuerySession session, String flag, String value, Query query) {
        if (value != null) {
            switch (value) {
                case "new":
                case "newest":
                case "desc":
                    session.setSortOrder(QuerySession.Sort.NEWEST_FIRST);
                    break;
                case "old":
                case "oldest":
                case "asc":
                default:
                    session.setSortOrder(QuerySession.Sort.OLDEST_FIRST);
            }
        } else {
            session.setSortOrder(QuerySession.Sort.OLDEST_FIRST);
        }
        return Optional.empty();
    }
}
