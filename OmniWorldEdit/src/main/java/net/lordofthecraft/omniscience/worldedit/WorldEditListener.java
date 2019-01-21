package net.lordofthecraft.omniscience.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.Subscribe;

public class WorldEditListener {

    @Subscribe
    public void onEditSessionChange(EditSessionEvent event) {
        if (event.getStage() == EditSession.Stage.BEFORE_CHANGE) {
            event.setExtent(new OmniscienceExtent(event.getExtent()));
        }
    }
}
