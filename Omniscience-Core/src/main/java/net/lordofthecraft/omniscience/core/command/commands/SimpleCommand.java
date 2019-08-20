package net.lordofthecraft.omniscience.core.command.commands;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.core.command.OmniSubCommand;

public abstract class SimpleCommand implements OmniSubCommand {
    private final ImmutableList<String> commands;

    public SimpleCommand(ImmutableList<String> commands) {
        this.commands = commands;
    }

    @Override
    public boolean isCommand(String command) {
        return getCommand().equalsIgnoreCase(command) || commands.contains(command);
    }
}
