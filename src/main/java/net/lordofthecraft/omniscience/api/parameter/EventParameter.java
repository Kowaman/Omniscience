package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.OmniEventRegistrar;
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
import java.util.stream.Collectors;

public class EventParameter extends BaseParameterHandler {
    //Credit to Prism for this regex
    private final Pattern pattern = Pattern.compile("[~|!]?[\\w,-]+");

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
        query.addCondition(FieldCondition.of(DataKeys.EVENT_NAME, MatchRule.EQUALS, value));

        return Optional.empty();
    }

    @Override
    public Optional<List<String>> suggestTabCompletion(String partial) {
        if (partial == null || partial.isEmpty()) {
            return Optional.of(Lists.newArrayList(OmniEventRegistrar.INSTANCE.getEventNames()));
        }
        String[] values = partial.split(",");
        String target = values[values.length - 1];
        return Optional.of(OmniEventRegistrar.INSTANCE.getEventNames().stream()
                .filter(event -> event.startsWith(target))
                .map(val -> {
                    StringBuilder builder = new StringBuilder();
                    if (values.length > 1) {
                        for (int i = 0; i < values.length - 2; i++) {
                            builder.append(values[i]).append(",");
                        }
                    }
                    builder.append(val);
                    return builder.toString();
                })
                .collect(Collectors.toList()));
    }
}
