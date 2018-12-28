package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.query.FieldCondition;
import net.lordofthecraft.omniscience.api.query.MatchRule;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.util.DataHelper;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockParameter extends BaseParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-\\\\*]+");

    public BlockParameter() {
        super(ImmutableList.of("b", "block"));
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
            query.addCondition(FieldCondition.of(DataKeys.TARGET, MatchRule.EQUALS, DataHelper.compileUserInput(value.toUpperCase())));
        }

        return Optional.empty();
    }

    @Override
    public Optional<List<String>> suggestTabCompletion(String partial) {
        Stream<Material> materialList = Lists.newArrayList(Material.values())
                .stream().filter(Material::isBlock);
        if (partial != null && !partial.isEmpty()) {
            String[] values = partial.split(",");
            final String target;
            if (values.length < 1) {
                target = partial;
            } else {
                target = values[values.length - 1];
            }
            Material possible = Material.matchMaterial(target.startsWith("!") ? target.substring(1) : target);
            if (possible != null) {
                if (values.length > 1) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < values.length - 1; i++) {
                        builder.append(values[i]).append(",");
                    }
                    builder.append(possible.name().toLowerCase());
                    return Optional.of(Collections.singletonList(builder.toString()));
                } else {
                    return Optional.of(Collections.singletonList(possible.name().toLowerCase()));
                }
            }
            return Optional.of(materialList
                    .filter(material -> target.startsWith("!") ? material.name().toLowerCase().contains(target.toLowerCase().substring(1)) : material.name().toLowerCase().contains(target.toLowerCase()))
                    .map(val -> {
                        StringBuilder builder = new StringBuilder();
                        if (values.length > 1) {
                            for (int i = 0; i < values.length - 1; i++) {
                                builder.append(values[i]).append(",");
                            }
                        }
                        builder.append(val.name().toLowerCase());
                        return builder.toString();
                    }).collect(Collectors.toList()));
        } else {
            return Optional.of(materialList.map(mat -> mat.name().toLowerCase()).collect(Collectors.toList()));
        }

    }
}
