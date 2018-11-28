package net.lordofthecraft.omniscience.api.data;

import java.util.Objects;

public final class DataKey {

    private final String key;

    private DataKey(String key) {
        this.key = key;
    }

    public static DataKey of(String value) {
        return new DataKey(value);
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataKey dataKey = (DataKey) o;
        return key.equals(dataKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
