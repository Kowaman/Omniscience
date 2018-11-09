package net.lordofthecraft.omniscience;

import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import org.bukkit.plugin.java.JavaPlugin;

public final class Omniscience extends JavaPlugin {

    private static OmniCore INSTANCE;

    public static IOmniscience getInstance() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = new OmniCore();
        INSTANCE.onLoad(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        INSTANCE.onDisable(this);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = new OmniCore();
        INSTANCE.onEnable(this);
    }
}
