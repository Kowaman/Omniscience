package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.query.FieldCondition;
import net.lordofthecraft.omniscience.api.query.MatchRule;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.util.DataHelper;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class IpParameter extends BaseParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w.:,-\\\\*]+");

    public IpParameter() {
        super(ImmutableList.of("ip"));
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
        if (value.contains(",")) {
            List<Pattern> in = Lists.newArrayList();
            List<Pattern> nin = Lists.newArrayList();
            for (String string : value.split(",")) {
                if (string.startsWith("!")) {
                    nin.add(DataHelper.compileUserInput(string.substring(1)));
                } else {
                    in.add(DataHelper.compileUserInput(string));
                }
            }
            if (!in.isEmpty()) {
                query.addCondition(FieldCondition.of(DataKeys.TARGET, MatchRule.INCLUDES, in));
            }
            if (!nin.isEmpty()) {
                query.addCondition(FieldCondition.of(DataKeys.TARGET, MatchRule.EXCLUDES, nin));
            }
        } else {
            query.addCondition(FieldCondition.of(DataKeys.TARGET, MatchRule.EQUALS, DataHelper.compileUserInput(value)));
        }


        return Optional.empty();
    }
}
