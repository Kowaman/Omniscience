package net.lordofthecraft.omniscience.api.data;

import com.google.common.collect.Maps;
import org.bukkit.block.Block;
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

    public static DataWrapper of(Block block) {
        DataWrapper wrapper = new DataWrapper();
        wrapper.set(X, block.getX());
        wrapper.set(Y, block.getY());
        wrapper.set(Z, block.getZ());
        wrapper.set(MATERIAL_TYPE, block.getType().name());
        //TODO We'll need a way to parse this. Return later when we know wtf this looks like.
        wrapper.set(BLOCK_DATA, block.getBlockData().getAsString());
        wrapper.set(WORLD, block.getWorld().getUID().toString());
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

    public Optional<Object> get(DataKey key) {
        return Optional.ofNullable(data.get(key.toString()));
    }

    public void set(DataKey key, Object value) {
        data.put(key.toString(), value);
    }

    public Set<DataKey> getKeys() {
        return data.keySet().stream().map(DataKey::of).collect(Collectors.toSet());
    }
}
