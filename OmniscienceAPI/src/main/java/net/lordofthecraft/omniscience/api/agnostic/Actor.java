package net.lordofthecraft.omniscience.api.agnostic;

import java.util.UUID;

public interface Actor {

    boolean hasPermission(String permission);

    OmniBlock getLocation();

    UUID getUUID();

    String getName();

}
