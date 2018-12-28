package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.OmniEventRegistrar;
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
import java.util.stream.Collectors;

public class EventParameter extends BaseParameterHandler {
    //Credit to Prism for this regex
    private final Pattern pattern = Pattern.compile("[!]?[\\w,-\\\\*]+");

    public EventParameter() {
        super(ImmutableList.of("e", "a", "event"));
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
                query.addCondition(FieldCondition.of(DataKeys.EVENT_NAME, MatchRule.INCLUDES, in));
            }
            if (!nin.isEmpty()) {
                query.addCondition(FieldCondition.of(DataKeys.EVENT_NAME, MatchRule.EXCLUDES, nin));
            }

        } else {
            query.addCondition(FieldCondition.of(DataKeys.EVENT_NAME, MatchRule.EQUALS, DataHelper.compileUserInput(value)));
        }

        return Optional.empty();
    }

    @Override
    public Optional<List<String>> suggestTabCompletion(String partial) {
        if (partial == null || partial.isEmpty()) {
            return Optional.of(Lists.newArrayList(OmniEventRegistrar.INSTANCE.getEventNames()));
        }
        String[] values = partial.split(",");
        final String target;
        if (values.length < 1) {
            target = partial;
        } else {
            target = values[values.length - 1];
        }
        return Optional.of(OmniEventRegistrar.INSTANCE.getEventNames().stream()
                .filter(event -> target.startsWith("!") ? event.startsWith(target.substring(1)) : event.startsWith(target))
                .map(val -> {
                    StringBuilder builder = new StringBuilder();
                    if (values.length > 1) {
                        for (int i = 0; i < values.length - 1; i++) {
                            builder.append(values[i]).append(",");
                        }
                    }
                    builder.append(val);
                    return builder.toString();
                })
                .collect(Collectors.toList()));
    }
}
