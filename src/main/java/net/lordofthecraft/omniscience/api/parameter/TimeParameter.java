package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.query.FieldCondition;
import net.lordofthecraft.omniscience.api.query.MatchRule;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.util.DateUtil;
import org.bukkit.command.CommandSender;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class TimeParameter extends BaseParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-]+");

    public TimeParameter() {
        super(ImmutableList.of("before", "since", "t"));
    }

    @Override
    public boolean canRun(CommandSender sender) {
        return true;
    }

    @Override
    public boolean acceptsValue(String value) {
        boolean matches = pattern.matcher(value).matches();

        if (matches) {
            try {
                DateUtil.parseTimeStringToDate(value, false);
            } catch (Exception ignored) {
                matches = false;
            }
        }

        return matches;
    }

    @Override
    public Optional<CompletableFuture<?>> buildForQuery(QuerySession session, String parameter, String value, Query query) {
        Date date = DateUtil.parseTimeStringToDate(value, false);

        MatchRule rule = MatchRule.LESS_THAN_EQUAL;
        if (parameter.equalsIgnoreCase("t") || parameter.equalsIgnoreCase("since")) {
            rule = MatchRule.GREATER_THAN_EQUAL;
        }

        query.addCondition(FieldCondition.of(DataKeys.CREATED, rule, date));

        return Optional.empty();
    }

    @Override
    public Optional<List<String>> suggestTabCompletion(String partial) {
        return Optional.empty();
    }

    //TODO default time parameter
}
