package net.lordofthecraft.omniscience.domain.block;

import com.mongodb.lang.Nullable;
import net.lordofthecraft.omniscience.domain.Actor;
import net.lordofthecraft.omniscience.domain.BaseEntry;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.Date;
import java.util.UUID;

public class BlockEntry extends BaseEntry {

    private BlockData fromData;
    private Material fromMaterial;
    private BlockData toData;
    private Material toMaterial;

    public BlockEntry(int x, int y, int z, UUID world, Actor actor, Date date) {
        super(x, y, z, world, actor, date);
    }

    public BlockEntry(int x, int y, int z, UUID world, Actor actor, Date date, @Nullable BlockData fromData, @Nullable BlockData toData) {
        super(x, y, z, world, actor, date);
        this.toData = toData;
        this.fromData = fromData;
        if (toData != null) {
            this.toMaterial = toData.getMaterial();
            this.toData = toData;
        }
        if (fromData != null) {
            this.fromMaterial = fromData.getMaterial();
            this.fromData = fromData;
        }
        if (fromData == null && toData == null) {
            throw new IllegalArgumentException("Both fromData and toData cannot be null");
        }
    }

    public Material getFromMaterial() {
        return fromMaterial;
    }

    public BlockData getFromData() {
        return fromData;
    }

    public Material getToMaterial() {
        return toMaterial;
    }

    public BlockData getToData() {
        return toData;
    }

    public boolean didBreak() {
        return toMaterial == null;
    }

    @Override
    public String getDiscriminator() {
        return "BLOCK";
    }

    @Override
    public void fromDocument(Document document) {
        this.fromMaterial = Material.getMaterial(document.getString("fromMaterial"));
        this.fromData = Bukkit.getServer().createBlockData(document.getString("fromData"));
        this.toMaterial = Material.getMaterial(document.getString("toMaterial"));
        this.toData = Bukkit.getServer().createBlockData(document.getString("toData"));
    }

    @Override
    protected void saveAdditionsToDocument(Document document) {
        document
                .append("fromMaterial", fromMaterial.name())
                .append("fromData", fromData.getAsString())
                .append("toMaterial", toMaterial.name())
                .append("toData", toData.getAsString());
    }
}
