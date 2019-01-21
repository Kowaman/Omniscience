package net.lordofthecraft.omniscience.worldedit;

import com.sk89q.worldedit.WorldEdit;
import net.lordofthecraft.omniscience.api.interfaces.WorldEditHandler;

public class DefaultWorldEditHandler implements WorldEditHandler {

    private final WorldEditListener listener;
    private final WorldEdit worldEdit;

    public DefaultWorldEditHandler() {
        worldEdit = WorldEdit.getInstance();
        listener = new WorldEditListener();
    }

    public void enableWorldEditLogging() {
        worldEdit.getEventBus().register(listener);
    }

    public void disableWorldEditLogging() {
        worldEdit.getEventBus().unregister(listener);
    }
}
