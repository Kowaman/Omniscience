package net.lordofthecraft.omniscience.api.data.datatype;

import net.lordofthecraft.omniscience.api.data.DataKey;

import java.util.Arrays;
import java.util.List;

public class KeyType implements DataType {

    private List<DataKey> keys;

    public KeyType(DataKey... keys) {
        this.keys = Arrays.asList(keys);
    }

    public List<DataKey> getKeys() {
        return keys;
    }
}
