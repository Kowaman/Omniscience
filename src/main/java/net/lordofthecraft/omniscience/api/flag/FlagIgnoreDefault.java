package net.lordofthecraft.omniscience.api.flag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FlagIgnoreDefault extends BaseFlagHandler {

    public FlagIgnoreDefault() {
        super(ImmutableList.of("nod", "igd", "ignoredefaults"));
    }

    @Override
    public boolean acceptsSource(CommandSender sender) {
        return false;
    }

    @Override
    public boolean requiresArguments() {
        return true;
    }

    @Override
    public Optional<List<String>> suggestCompletionOptions(String partial) {
        String[] split = partial.split(",");
        String target = split[split.length - 1];
        List<String> suggestions = Lists.newArrayList();
        Omniscience.getParameters().stream()
                .flatMap(pm -> pm.getAliases().stream())
                .filter(alias -> alias.toLowerCase().startsWith(target.toLowerCase()))
                .forEach(alias -> {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < split.length - 1; i++) {
                        builder.append(split[i]).append(",");
                    }
                    suggestions.add(builder.append(alias).toString());
                });
        return Optional.of(suggestions);
    }

    @Override
    public boolean acceptsValue(String value) {
        String[] split = value.split(",");
        for (String param : split) {
            if (!Omniscience.getParameterHandler(param).isPresent()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Optional<CompletableFuture<?>> process(QuerySession session, String flag, String value, Query query) {
        String[] split = value.split(",");
        for (String param : split) {
            Omniscience.getParameterHandler(param)
                    .ifPresent(session::addIgnoredDefault);
        }
        return Optional.empty();
    }
}
