package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.OmniConfig;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.api.query.SearchConditionGroup;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RadiusParameter extends BaseParameterHandler {

    public RadiusParameter() {
        super(ImmutableList.of("r", "radius"));
    }

    @Override
    public boolean canRun(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public boolean acceptsValue(String value) {
        return NumberUtils.isDigits(value);
    }

    @Override
    public Optional<CompletableFuture<?>> buildForQuery(QuerySession session, String parameter, String value, Query query) {
        if (session.getSender() instanceof Player) {
            Player player = (Player) session.getSender();
            Location location = player.getLocation();

            int radius = Integer.parseInt(value);
            int maxRadius = OmniConfig.INSTANCE.getRadiusLimit();
            if (radius > maxRadius && !player.hasPermission("omniscience.override.maxradius")) {
                player.sendMessage(String.format(ChatColor.RED + "Limiting radius to %s", maxRadius));
                radius = maxRadius;
            }

            session.setRadius(radius);

            query.addCondition(SearchConditionGroup.from(location, radius));
        }

        return Optional.empty();
    }

    //TODO process default
}
