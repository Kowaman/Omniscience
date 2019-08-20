package net.lordofthecraft.omniscience.core.api.parameter;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.parameter.BaseParameterHandler;
import net.lordofthecraft.omniscience.api.query.FieldCondition;
import net.lordofthecraft.omniscience.api.query.MatchRule;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.api.util.DateUtil;
import net.lordofthecraft.omniscience.core.OmniConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.CommandSender;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class TimeParameter extends BaseParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-]+");

    public TimeParameter() {
        super(ImmutableList.of("since", "t"));
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
    public Optional<Pair<String, String>> processDefault(QuerySession session, Query query) {
        String since = OmniConfig.INSTANCE.getDefaultSearchTime();

        try {
            Date date = DateUtil.parseTimeStringToDate(since, false);
            query.addCondition(FieldCondition.of(DataKeys.CREATED, MatchRule.GREATER_THAN_EQUAL, date));
            return Optional.of(Pair.of("since", since));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean doesConflict(Pair<String, String> parameterValue, Pair<String, String> otherParameter) {
        if (parameterValue.getKey().equalsIgnoreCase("t") || parameterValue.getKey().equalsIgnoreCase("since")) {
            return otherParameter.getKey().equalsIgnoreCase("t") || otherParameter.getKey().equalsIgnoreCase("since");
        } else {
            return otherParameter.getKey().equalsIgnoreCase("before");
        }
    }
}
