package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;

public interface ParameterHandler {

    boolean canRun(CommandSender sender);

    boolean acceptsValue(String value);

    boolean canHandle(String cmd);

    ImmutableList<String> getAliases();
}
