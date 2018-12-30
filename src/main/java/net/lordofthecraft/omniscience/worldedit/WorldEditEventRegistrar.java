package net.lordofthecraft.omniscience.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.lordofthecraft.omniscience.listener.worldedit.WorldEditLogger;
import org.bukkit.plugin.Plugin;

/**
 * We run our {@link com.sk89q.worldedit.util.eventbus.EventBus} registration here to avoid {@link NoClassDefFoundError}'s when enabling.
 *
 * @author 501warhead
 */
public class WorldEditEventRegistrar {

    public static void registerWorldEditListener(Plugin plugin) {
        if (!(plugin instanceof WorldEditPlugin)) {
            return;
        }
        WorldEdit worldEdit = ((WorldEditPlugin) plugin).getWorldEdit();
        worldEdit.getEventBus().register(new WorldEditLogger());
    }
}
