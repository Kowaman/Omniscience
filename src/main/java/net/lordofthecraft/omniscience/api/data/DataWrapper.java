package net.lordofthecraft.omniscience.api.data;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.lordofthecraft.omniscience.api.data.DataKey.of;
import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

public final class DataWrapper {
    private final Map<String, Object> data = Maps.newLinkedHashMap();
    private final DataKey key;
    private final DataWrapper parent;

    private DataWrapper(DataWrapper parent, DataKey key) {
        this.parent = parent;

        this.key = key;
    }

    private DataWrapper() {
        this.key = of();
        this.parent = this;
    }

    public static DataWrapper createNew() {
        return new DataWrapper();
    }

    public static DataWrapper ofBlock(BlockState block) {
        DataWrapper wrapper = new DataWrapper();
        wrapper.set(MATERIAL_TYPE, block.getType().name());
        //TODO We'll need a way to parse this. Return later when we know wtf this looks like.
        wrapper.set(BLOCK_DATA, block.getBlockData().getAsString());
        return wrapper;
    }

    public static DataWrapper ofEntity(Entity entity) {
        DataWrapper wrapper = new DataWrapper();
        //TODO flesh out
        wrapper.set(ENTITY_TYPE, entity.getType().name());
        wrapper.set(WORLD, entity.getWorld().getUID().toString());
        return wrapper;
    }

    public static DataWrapper ofConfig(ConfigurationSerializable configurationSerializable) {
        DataWrapper wrapper = new DataWrapper();
        Map<String, Object> data = configurationSerializable.serialize();
        data.forEach((key, value) -> {
            DataKey dataKey = DataKey.of(key);
            if (value instanceof ConfigurationSerializable) {
                wrapper.set(dataKey, ofConfig((ConfigurationSerializable) value));
            } else {
                wrapper.set(dataKey, value);
            }
        });
        return wrapper;
    }

    public static DataWrapper ofBlockData(BlockData data) {
        //TODO flesh out making a datawrapper of blockdata
        String blockData = data.getAsString();
        String[] splitData = blockData.split("\\[");
        String blockName = splitData[0].split(":")[1];
        if (splitData.length > 1) {

        }
        return null;
    }

    public DataKey getKey() {
        return key;
    }

    public Optional<DataWrapper> getParent() {
        return Optional.ofNullable(this.parent);
    }

    public String getName() {
        List<String> parts = this.key.getParts();
        return parts.isEmpty() ? "" : parts.get(parts.size() - 1);
    }

    public Optional<Object> get(DataKey key) {
        List<String> queryParts = key.getParts();

        int size = queryParts.size();

        if (size == 0) {
            return Optional.of(this);
        }

        String rootKey = queryParts.get(0);
        if (size == 1) {
            final Object object = this.data.get(rootKey);
            if (object == null) {
                return Optional.empty();
            }
            return Optional.of(object);
        }
        Optional<DataWrapper> oSubWrapper = this.getUnsafeWrapper(rootKey);
        if (!oSubWrapper.isPresent()) {
            return Optional.empty();
        }
        DataWrapper subWrapper = oSubWrapper.get();
        return subWrapper.get(key.popFirst());
    }

    public DataWrapper set(DataKey key, Object value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        List<String> parts = key.getParts();
        String rootKey = parts.get(0);
        if (parts.size() > 1) {
            DataKey subKey = of(parts);
            Optional<DataWrapper> oSubWrapper = this.getUnsafeWrapper(subKey);
            DataWrapper subWrapper;
            if (!oSubWrapper.isPresent()) {
                this.createWrapper(subKey);
                subWrapper = (DataWrapper) this.data.get(rootKey);
            } else {
                subWrapper = oSubWrapper.get();
            }
            subWrapper.set(key.popFirst(), value);
        }
        if (value instanceof DataWrapper) {
            checkArgument(value != this, "Cannot set a DataWrapper to itself");

            copyDataWrapper(key, (DataWrapper) value);
        } else if (value instanceof Map) {
            setMap(rootKey, (Map) value);
        } else {
            this.data.put(rootKey, value);
        }
        return this;
    }

    private void setMap(String key, Map<?, ?> value) {
        DataWrapper wrapper = createWrapper(of(key));
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            wrapper.set(of(entry.getKey().toString()), entry.getValue());
        }
    }

    private void copyDataWrapper(DataKey key, DataWrapper value) {
        Collection<DataKey> valueKeys = value.getKeys(true);
        for (DataKey oldKey : valueKeys) {
            set(key.then(oldKey), value.get(oldKey).get());
        }
    }

    public DataWrapper remove(DataKey key) {
        checkNotNull(key, "key");
        List<String> parts = key.getParts();
        if (parts.size() > 1) {
            String subKey = parts.get(0);
            DataKey subDataKey = of(subKey);
            Optional<DataWrapper> oWrapper = this.getUnsafeWrapper(subDataKey);
            if (!oWrapper.isPresent()) {
                return this;
            }
            DataWrapper subWrapper = oWrapper.get();
            subWrapper.remove(key.popFirst());
        } else {
            this.data.remove(parts.get(0));
        }
        return this;
    }

    public DataWrapper createWrapper(DataKey key) {
        List<String> keyParts = key.getParts();

        int size = keyParts.size();

        checkArgument(size != 0, "The size of the key must be at least 1");

        String rootKey = keyParts.get(0);
        DataKey rootDataKey = of(rootKey);

        if (size == 1) {
            DataWrapper result = new DataWrapper(parent, rootDataKey);
            this.data.put(rootKey, result);
            return result;
        }
        DataKey subKey = key.popFirst();
        DataWrapper subWrapper = (DataWrapper) this.data.get(rootKey);
        if (subWrapper == null) {
            subWrapper = new DataWrapper(this.parent, rootDataKey);
            this.data.put(rootKey, subWrapper);
        }
        return subWrapper.createWrapper(subKey);
    }

    public Optional<DataWrapper> getWrapper(DataKey key) {
        return get(key).filter(obj -> obj instanceof DataWrapper).map(obj -> (DataWrapper) obj);
    }

    private Optional<DataWrapper> getUnsafeWrapper(DataKey key) {
        return get(key).filter(obj -> obj instanceof DataWrapper).map(obj -> (DataWrapper) obj);
    }

    private Optional<DataWrapper> getUnsafeWrapper(String key) {
        final Object object = this.data.get(key);
        if (!(object instanceof DataWrapper)) {
            return Optional.empty();
        }
        return Optional.of((DataWrapper) object);
    }

    public Set<DataKey> getKeys(boolean deep) {
        ImmutableSet.Builder<DataKey> builder = ImmutableSet.builder();

        for (Map.Entry<String, Object> entry : this.data.entrySet()) {
            builder.add(of(entry.getKey()));
        }
        if (deep) {
            for (Map.Entry<String, Object> entry : this.data.entrySet()) {
                if (entry.getValue() instanceof DataWrapper) {
                    for (DataKey key : ((DataWrapper) entry.getValue()).getKeys(true)) {
                        builder.add(of(entry.getKey()).then(key));
                    }
                }
            }
        }
        return builder.build();
    }

    public Optional<Boolean> getBoolean(DataKey key) {
        return get(key).map(obj -> (Boolean) obj);
    }

    public Optional<String> getString(DataKey key) {
        return get(key).map(obj -> (String) obj);
    }

    public Optional<Integer> getInt(DataKey key) {
        return get(key).map(obj -> (Integer) obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataWrapper)) return false;
        DataWrapper wrapper = (DataWrapper) o;
        return Objects.equals(data.entrySet(), wrapper.data.entrySet()) &&
                Objects.equals(key, wrapper.key);
    }

    @Override
    public String toString() {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
        if (!this.key.toString().isEmpty()) {
            helper.add("key", this.key);
        }
        return helper.add("data", this.data).toString();
    }
}
