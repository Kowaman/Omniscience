package net.lordofthecraft.omniscience.api.data.json;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.lordofthecraft.omniscience.api.data.DataKey;
import net.lordofthecraft.omniscience.api.data.DataWrapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class DataWrapperDeserializer implements JsonDeserializer<DataWrapper> {

    private static final Gson gson = new Gson();

    @Override
    public DataWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return buildWrapper(jsonObject);
    }

    private DataWrapper buildWrapper(JsonObject jsonObject) {
        DataWrapper wrapper = DataWrapper.createNew();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            DataKey key = DataKey.of(entry.getKey());
            if (entry.getValue().isJsonObject()) {
                wrapper.set(key, buildWrapper(entry.getValue().getAsJsonObject()));
            } else if (entry.getValue().isJsonArray()) {
                JsonArray array = entry.getValue().getAsJsonArray();
                wrapper.set(key, buildArray(array));
            } else {
                wrapper.set(key, gson.fromJson(entry.getValue(), Object.class));
            }
        }
        return wrapper;
    }

    private List<Object> buildArray(JsonArray array) {
        List<Object> list = Lists.newArrayList();
        array.forEach(jsonElement -> {
            if (jsonElement.isJsonObject()) {
                list.add(buildWrapper(jsonElement.getAsJsonObject()));
            } else if (jsonElement.isJsonArray()) {
                list.addAll(buildArray(array));
            } else {
                list.add(gson.fromJson(jsonElement, Object.class));
            }
        });
        return list;
    }
}
