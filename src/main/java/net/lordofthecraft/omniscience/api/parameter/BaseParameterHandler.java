package net.lordofthecraft.omniscience.api.parameter;

import com.google.common.collect.ImmutableList;

public abstract class BaseParameterHandler implements ParameterHandler {

    protected final ImmutableList<String> aliases;

    public BaseParameterHandler(ImmutableList<String> aliases) {
        this.aliases = aliases;
    }

    @Override
    public boolean canHandle(String cmd) {
        return aliases.contains(cmd);
    }

    @Override
    public ImmutableList<String> getAliases() {
        return aliases;
    }
}
