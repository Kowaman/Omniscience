package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.query.FieldCondition;
import net.lordofthecraft.omniscience.api.query.MatchRule;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerParameter extends BaseParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-]+");

    public PlayerParameter() {
        super(ImmutableList.of("p", "player"));
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
        OfflinePlayer player = Bukkit.getOfflinePlayer(value);

        if (player != null) {
            query.addCondition(FieldCondition.of(DataKeys.PLAYER_ID, MatchRule.EQUALS, player.getUniqueId().toString()));
        }

        return Optional.empty();
    }

    @Override
    public Optional<List<String>> suggestTabCompletion(String partial) {
        Stream<? extends Player> playerStream = Bukkit.getOnlinePlayers().stream();
        if (partial != null && !partial.isEmpty()) {
            playerStream = playerStream.filter(player -> player.getName().toLowerCase().startsWith(partial.toLowerCase()));
        }
        return Optional.of(playerStream.map(Player::getName).collect(Collectors.toList()));
    }
}
