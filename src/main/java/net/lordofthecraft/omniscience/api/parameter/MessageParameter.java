package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
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

public class MessageParameter extends BaseParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w!,:-\\\\*]+");

    public MessageParameter() {
        super(ImmutableList.of("msg", "m", "text", "message"));
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
            query.addCondition(FieldCondition.of(DataKeys.MESSAGE, MatchRule.EQUALS, compileMessageSearch(value.split(","))));
        } else {
            query.addCondition(FieldCondition.of(DataKeys.MESSAGE, MatchRule.EQUALS, DataHelper.compileUserInput(value)));
        }

        return Optional.empty();
    }

    @Override
    public Optional<List<String>> suggestTabCompletion(String partial) {
        return Optional.empty();
    }

    private Pattern compileMessageSearch(String[] messages) {
        StringBuilder exclusionBuilder = new StringBuilder();
        StringBuilder searchBuilder = new StringBuilder();
        searchBuilder.append("^.*(");
        exclusionBuilder.append("(?!.*?(");
        boolean excluded = false;
        boolean firstSearch = false;
        for (String string : messages) {
            //Prevent regex from being fucked by user input.
            String lString = string.replaceAll("[-.\\+*?\\[^\\]$(){}=!<>|:\\\\]", "\\\\$0");
            if (string.startsWith("!")) {
                if (!excluded) {
                    exclusionBuilder.append(lString.substring(2).replaceAll("\\*", ".*"));
                    excluded = true;
                } else {
                    exclusionBuilder.append("|").append(lString.substring(2).replaceAll("\\*", ".*"));
                }
            } else {
                if (!firstSearch) {
                    searchBuilder.append(lString);
                    firstSearch = true;
                } else {
                    searchBuilder.append("|").append(lString);
                }
            }
        }
        exclusionBuilder.append("))");
        searchBuilder.append(")+.*$");
        if (excluded) {
            return Pattern.compile(exclusionBuilder.toString() + searchBuilder.toString());
        } else {
            return Pattern.compile(searchBuilder.toString());
        }
    }
}
