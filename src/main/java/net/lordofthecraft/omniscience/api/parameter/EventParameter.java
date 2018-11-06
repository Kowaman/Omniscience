package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class EventParameter extends BaseParameterHandler {
    //Credit to Prism for this regex
    private final Pattern pattern = Pattern.compile("[~|!]?[\\w,-]+");

    public EventParameter() {
        super(ImmutableList.of("e", "a"));
    }

    @Override
    public boolean canRun(CommandSender sender) {
        return true;
    }

    @Override
    public boolean acceptsValue(String value) {
        return pattern.matcher(value).matches();
    }
}
