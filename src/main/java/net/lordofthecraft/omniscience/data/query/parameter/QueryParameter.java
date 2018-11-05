package net.lordofthecraft.omniscience.data.query.parameter;

import org.bson.Document;
import org.bukkit.Material;

public interface QueryParameter {

    static QueryParameter byMaterial(Material material) {
        return new MaterialParameter(material);
    }

    Document getQueryDocument();

}
