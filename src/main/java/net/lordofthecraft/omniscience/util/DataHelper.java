package net.lordofthecraft.omniscience.util;

import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
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

    public static Optional<ItemStack> getItemStackFromWrapper(DataWrapper wrapper) {
        if (!wrapper.getKeys().contains(ITEMSTACK)) {
            return Optional.empty();
        }
        Optional<Object> oObject = wrapper.get(ITEMSTACK);
        if (oObject.isPresent()) {
            Object object = oObject.get();
            if (object instanceof Map) {
                return Optional.ofNullable(ItemStack.deserialize((Map<String, Object>) object));
            }
        }
        return Optional.empty();
    }

    public static Optional<BlockData> getBlockDataFromWrapper(DataWrapper wrapper) {
        if (!wrapper.getKeys().contains(BLOCK_DATA)) {
            return Optional.empty();
        }
        return wrapper
                .get(BLOCK_DATA)
                .map(o -> Bukkit.getServer()
                        .createBlockData((String) o));
    }

    public static Optional<Location> getLocationFromDataWrapper(DataWrapper wrapper) {
        if (!wrapper.getKeys().containsAll(Arrays.asList(X, Y, Z, WORLD))) {
            return Optional.empty();
        }
        Optional<Integer> oX = wrapper.get(X);
        Optional<Integer> oY = wrapper.get(Y);
        Optional<Integer> oZ = wrapper.get(Z);
        Optional<String> oWorld = wrapper.get(WORLD);
        if (oX.isPresent()
                && oY.isPresent()
                && oZ.isPresent()
                && oWorld.isPresent()) {
            Location location = new Location(Bukkit.getWorld(UUID.fromString(oWorld.get())), oX.get(), oY.get(), oZ.get());
            return Optional.of(location);
        }
        return Optional.empty();
    }

    public static Optional<Entity> getEntityFromWrapper(DataWrapper wrapper) throws IllegalAccessException {
        //TODO there's no way this can return a raw entity. Likely will need to do something special for this method.
        throw new IllegalAccessException("this method is not yet implemented");
    }

    public static BaseComponent[] buildLocation(Location location, boolean clickable) {
        ComponentBuilder builder = new ComponentBuilder("(x: " + location.getBlockX() + " y: " + location.getBlockY() + " z: " + location.getBlockZ());
        builder.append(" world: " + location.getWorld().getName());
        if (clickable) {
            builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/omnitele " + location.getWorld().getUID().toString() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()));
        }
        return builder.append(")").create();
    }
}
