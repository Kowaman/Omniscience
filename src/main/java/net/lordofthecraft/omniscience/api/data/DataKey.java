package net.lordofthecraft.omniscience.api.data;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public final class DataKey {

    private final ImmutableList<String> parts;

    private ImmutableList<DataKey> keyParts;

    private DataKey(String... parts) {
        this.parts = ImmutableList.copyOf(parts);
    }

    private DataKey(List<String> parts) {
        this.parts = ImmutableList.copyOf(parts);
    }

    public static DataKey of(String... values) {
        return new DataKey(values);
    }

    public static DataKey of(List<String> values) {
        return new DataKey(values);
    }

    public ImmutableList<String> getParts() {
        return parts;
    }

    public DataKey then(DataKey that) {
        ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();

        builder.addAll(this.parts);
        builder.addAll(that.parts);

        return new DataKey(builder.build());
    }

    public DataKey then(String that) {
        ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();

        builder.addAll(this.parts);
        builder.add(that);

        return new DataKey(builder.build());
    }

    public List<DataKey> getKeyParts() {
        if (this.keyParts == null) {
            ImmutableList.Builder<DataKey> builder = ImmutableList.builder();
            for (String part : getParts()) {
                builder.add(new DataKey(part));
            }
            this.keyParts = builder.build();
        }
        return this.keyParts;
    }

    public DataKey pop() {

        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (int i = 0; i < this.parts.size() - 1; i++) {
            builder.add(this.parts.get(i));
        }
        return new DataKey(builder.build());
    }

    public DataKey popFirst() {

        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (int i = 1; i < this.parts.size(); i++) {
            builder.add(this.parts.get(i));
        }
        return new DataKey(builder.build());
    }

    public DataKey last() {
        if (this.parts.size() <= 1) {
            return this;
        }
        return new DataKey(this.parts.get(this.parts.size() - 1));
    }

    public String asString(String seperator) {
        return Joiner.on(seperator).join(this.parts);
    }

    public String asString(char separator) {
        return asString(String.valueOf(separator));
    }

    @Override
    public int hashCode() {
        return Objects.hash(parts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataKey)) return false;
        DataKey dataKey = (DataKey) o;
        return Objects.equals(parts, dataKey.parts);
    }

    @Override
    public String toString() {
        return asString('.');
    }
}
