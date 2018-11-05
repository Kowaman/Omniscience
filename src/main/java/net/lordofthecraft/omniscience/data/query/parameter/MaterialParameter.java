package net.lordofthecraft.omniscience.data.query.parameter;

import org.bson.Document;
import org.bukkit.Material;

public class MaterialParameter extends BaseParameter {

    private final Material material;

    public MaterialParameter(Material material) {
        this.material = material;
    }

    @Override
    protected void addAdditionalQueriesToDocument(Document document) {

    }
}
