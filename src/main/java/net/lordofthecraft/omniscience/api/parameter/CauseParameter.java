package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.query.FieldCondition;
import net.lordofthecraft.omniscience.api.query.MatchRule;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class CauseParameter extends BaseParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-]+");

    public CauseParameter() {
        super(ImmutableList.of("c", "cause"));
    }

    @Override
    public boolean canRun(CommandSender sender) {
        return true;
    }

    @Override
    public boolean acceptsValue(String value) {
        return pattern.matcher(value).matches();
    }

    @Override
    public Optional<CompletableFuture<?>> buildForQuery(QuerySession session, String parameter, String value, Query query) {
        query.addCondition(FieldCondition.of(DataKeys.CAUSE, MatchRule.EQUALS, Pattern.compile(value)));

        return Optional.empty();
    }

    @Override
    public Optional<List<String>> suggestTabCompletion(String partial) {
        return Optional.empty();
    }
}
