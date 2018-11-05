package net.lordofthecraft.omniscience;

import net.lordofthecraft.omniscience.domain.DataMapper;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Omniscience extends JavaPlugin implements IOmniscience {

    private static Omniscience INSTANCE;

    private MongoConnectionHandler connectionHandler;

    public static IOmniscience getInstance() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        connectionHandler.getClient().close();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        saveDefaultConfig();
        this.connectionHandler = MongoConnectionHandler.createHandler(getConfig());
        DataMapper.INSTANCE.initInternal();
    }
}
