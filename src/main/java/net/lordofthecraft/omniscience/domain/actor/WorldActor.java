package net.lordofthecraft.omniscience.domain.actor;

import net.lordofthecraft.omniscience.domain.Actor;

public class WorldActor implements Actor {

    private WorldActor() {
    }

    public static WorldActor get() {
        return new WorldActor();
    }

    @Override
    public String getName() {
        return "WORLD";
    }

    @Override
    public String getKey() {
        return "WORLD";
    }
}
