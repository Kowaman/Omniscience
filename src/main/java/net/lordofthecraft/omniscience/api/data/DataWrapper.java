package net.lordofthecraft.omniscience.api.data;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

public final class DataWrapper {
    private Map<String, Object> data = Maps.newHashMap();

    private DataWrapper() {

    }

    public static DataWrapper createNew() {
        return new DataWrapper();
    }

    public static DataWrapper of(BlockState block) {
        DataWrapper wrapper = new DataWrapper();
        DataWrapper location = new DataWrapper();
        location.set(X, block.getX());
        location.set(Y, block.getY());
        location.set(Z, block.getZ());
        location.set(WORLD, block.getWorld().getUID().toString());
        wrapper.set(LOCATION, location);
        wrapper.set(MATERIAL_TYPE, block.getType().name());
        //TODO We'll need a way to parse this. Return later when we know wtf this looks like.
        wrapper.set(BLOCK_DATA, block.getBlockData().getAsString());
        return wrapper;
    }

    public static DataWrapper of(Entity entity) {
        DataWrapper wrapper = new DataWrapper();
        //TODO flesh out
        wrapper.set(ENTITY_TYPE, entity.getType().name());
        wrapper.set(WORLD, entity.getWorld().getUID().toString());
        return wrapper;
    }

    public static DataWrapper of(ItemStack itemStack) {
        DataWrapper wrapper = new DataWrapper();
        DataWrapper itemData = new DataWrapper();
        Map<String, Object> data = itemStack.serialize();
        data.forEach((key, value) -> {
            DataKey dataKey = DataKey.of(key);
            itemData.set(dataKey, itemData);
        });
        wrapper.set(ITEMSTACK, itemData);
        wrapper.set(MATERIAL_TYPE, itemStack.getType().name());
        return wrapper;
    }

    public static DataWrapper of(Location location) {
        DataWrapper wrapper = new DataWrapper();
        wrapper.set(X, location.getBlockX());
        wrapper.set(Y, location.getBlockY());
        wrapper.set(Z, location.getBlockZ());
        wrapper.set(WORLD, location.getWorld().getUID().toString());
        return wrapper;
    }

    public <T> Optional<T> get(DataKey key) {
        Object object = data.get(key.toString());
        try {
            return Optional.ofNullable((T) data.get(key.toString()));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public void set(DataKey key, Object value) {
        data.put(key.toString(), value);
    }

    public Set<DataKey> getKeys() {
        return data.keySet().stream().map(DataKey::of).collect(Collectors.toSet());
    }
}
