package net.lordofthecraft.omniscience.core.api.entry.entrybuilder;

import net.lordofthecraft.omniscience.api.entry.entrybuilder.SourceBuilder;

public class BukkitSourceBuilder implements SourceBuilder {
    private final Object object;

    public BukkitSourceBuilder(Object object) {
        this.object = object;
    }

    @Override
    public Object getSource() {
        return object;
    }
}
