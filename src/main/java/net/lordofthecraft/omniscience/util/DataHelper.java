package net.lordofthecraft.omniscience.util;

import com.google.common.collect.Maps;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

public final class DataHelper {

    public static boolean isPrimitiveType(Object object) {
        return (object instanceof Boolean ||
                object instanceof Byte ||
                object instanceof Character ||
                object instanceof Double ||
                object instanceof Float ||
                object instanceof Integer ||
                object instanceof Long ||
                object instanceof Short ||
                object instanceof String);
    }

    public static Optional<BlockData> getBlockDataFromWrapper(DataWrapper wrapper) {
        if (!wrapper.getKeys(false).contains(BLOCK_DATA)) {
            return Optional.empty();
        }
        return wrapper
                .get(BLOCK_DATA)
                .map(o -> Bukkit.getServer()
                        .createBlockData((String) o));
    }

    public static Optional<Location> getLocationFromDataWrapper(DataWrapper wrapper) {
        if (!wrapper.get(LOCATION).isPresent()) {
            return Optional.empty();
        }
        Optional<Integer> oX = wrapper.getInt(LOCATION.then(X));
        Optional<Integer> oY = wrapper.getInt(LOCATION.then(Y));
        Optional<Integer> oZ = wrapper.getInt(LOCATION.then(Z));
        Optional<String> oWorld = wrapper.getString(LOCATION.then(WORLD));
        if (oX.isPresent()
                && oY.isPresent()
                && oZ.isPresent()
                && oWorld.isPresent()) {
            Location location = new Location(Bukkit.getWorld(UUID.fromString(oWorld.get())), oX.get(), oY.get(), oZ.get());
            return Optional.of(location);
        }
        return Optional.empty();
    }

    public static Optional<String[]> getSignTextFromWrapper(DataWrapper wrapper) {
        if (!wrapper.get(SIGN_TEXT).isPresent()) {
            return Optional.empty();
        }
        Optional<String> oString = wrapper.getString(SIGN_TEXT);
        return oString.map(SerializeHelper::deserializeStringArray);
    }

    public static <T extends ConfigurationSerializable> T unwrapConfigSerializable(DataWrapper wrapper) {
        Optional<String> oClassName = wrapper.getString(CONFIG_CLASS);
        if (!oClassName.isPresent()) {
            return null;
        }
        String fullClassName = oClassName.get();
        try {
            Class clazz = Class.forName(fullClassName);
            DataWrapper localWrapper = wrapper.copy().remove(CONFIG_CLASS);
            Map<String, Object> configMap = Maps.newHashMap();
            localWrapper.getKeys(false)
                    .forEach(key -> localWrapper.get(key)
                            .ifPresent(val -> {
                                if (val instanceof DataWrapper) {
                                    configMap.put(key.toString(), unwrapConfigSerializable((DataWrapper) val));
                                } else {
                                    configMap.put(key.toString(), val);
                                }
                            }));
            ConfigurationSerializable config = ConfigurationSerialization.deserializeObject(configMap, clazz);
            return (T) config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertItemList(ItemStack[] items) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("configdata", items);
        return configuration.saveToString();
    }

    public static BaseComponent[] buildLocation(Location location, boolean clickable) {
        ComponentBuilder builder = new ComponentBuilder("(x: " + location.getBlockX() + " y: " + location.getBlockY() + " z: " + location.getBlockZ()).color(ChatColor.GRAY);
        builder.append(" world: " + location.getWorld().getName()).color(ChatColor.GRAY);
        if (clickable) {
            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to teleport").color(ChatColor.GRAY).italic(true).create()));
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/omnitele " + location.getWorld().getUID().toString() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()));
        }
        return builder.append(")").color(ChatColor.GRAY).create();
    }
}
