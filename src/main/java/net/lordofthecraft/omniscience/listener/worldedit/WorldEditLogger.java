package net.lordofthecraft.omniscience.listener.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.worldedit.OmniscienceExtent;

import java.util.logging.Level;

/**
 * Log world edit changes via {@link OmniscienceExtent}
 *
 * @author 501warhead
 */
public class WorldEditLogger {

    @Subscribe
    public void onExtent(EditSessionEvent event) {
        try {
            if (event.getStage() == EditSession.Stage.BEFORE_CHANGE) {
                event.setExtent(new OmniscienceExtent(event.getExtent(), event.getWorld(), event.getActor()));
            }
        } catch (Exception e) {
            Omniscience.getPluginInstance().getLogger().log(Level.SEVERE, "Failed to set extent.", e);
        }
    }

}
