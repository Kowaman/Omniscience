package net.lordofthecraft.omniscience.api.data;

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
}
