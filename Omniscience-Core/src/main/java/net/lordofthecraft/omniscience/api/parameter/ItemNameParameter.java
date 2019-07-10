package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.data.DataKey;
import net.lordofthecraft.omniscience.api.query.FieldCondition;
import net.lordofthecraft.omniscience.api.query.MatchRule;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.api.util.DataHelper;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static net.lordofthecraft.omniscience.api.data.DataKeys.DATA;
import static net.lordofthecraft.omniscience.api.data.DataKeys.ITEMSTACK;


public class ItemNameParameter extends BaseParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w!,:-\\\\*]+");

    public ItemNameParameter() {
        super(ImmutableList.of("n"));
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
        DataKey name = DATA.then(ITEMSTACK).then(DataKey.of("meta")).then(DataKey.of("display-name"));
        if (value.contains(",")) {
            query.addCondition(FieldCondition.of(name, MatchRule.EQUALS, compileMessageSearch(value.split(","))));
        } else {
            query.addCondition(FieldCondition.of(name, MatchRule.EQUALS, DataHelper.compileUserInput(value)));
        }

        return Optional.empty();
    }
}
