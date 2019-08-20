package net.lordofthecraft.omniscience.core.io.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.api.data.DataKey;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.api.util.DataHelper;
import net.lordofthecraft.omniscience.core.Omniscience;
import net.lordofthecraft.omniscience.core.io.RecordHandler;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static net.lordofthecraft.omniscience.api.data.DataKeys.PLAYER_ID;

public class DynamoRecordHandler implements RecordHandler {

    private final DynamoStorageHandler storageHandler;

    public DynamoRecordHandler(DynamoStorageHandler storageHandler) {
        this.storageHandler = storageHandler;
    }

    @Override
    public void write(List<DataWrapper> wrappers) {
        List<Item> items = wrappers.stream().map(this::wrapperToItem).collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<DataEntry>> query(QuerySession session) {
        AmazonDynamoDB db = storageHandler.getDynamoDB();
        QueryRequest request = new QueryRequest();
        return null;
    }

    private Item wrapperToItem(DataWrapper wrapper) {
        Item item = new Item();

        Set<DataKey> keys = wrapper.getKeys(false);
        for (DataKey dataKey : keys) {
            Optional<Object> oObject = wrapper.get(dataKey);
            oObject.ifPresent(object -> {
                String key = dataKey.asString(".");
                if (object instanceof List) {
                    List<Object> convertedList = Lists.newArrayList();
                    for (Object innerObject : (List<?>) object) {
                        if (innerObject instanceof DataWrapper) {
                            convertedList.add(wrapperToItem((DataWrapper) innerObject));
                        } else if (DataHelper.isPrimitiveType(innerObject)) {
                            convertedList.add(innerObject);
                        } else if (object.getClass().isEnum()) {
                            convertedList.add(object.toString());
                        } else {
                            Omniscience.getPluginInstance().getLogger().warning("Unsupported List Data Type: " + innerObject.getClass().getName());
                        }
                    }

                    if (!convertedList.isEmpty()) {
                        item.with(key, convertedList);
                    }
                } else if (object instanceof DataWrapper) {
                    DataWrapper subWrapper = (DataWrapper) object;
                    item.with(key, wrapperToItem(subWrapper));
                } else {
                    if (key.equals(PLAYER_ID.toString())) {
                        item.with(PLAYER_ID.toString(), object);
                    } else {
                        item.with(key, object);
                    }
                }
            });
        }

        return item;
    }

    private DataWrapper itemToDataWrapper(Item item) {
        DataWrapper wrapper = DataWrapper.createNew();

        item.asMap().forEach((key, value) -> {
            DataKey dataKey = DataKey.of(key);
            if (value instanceof Item) {
                wrapper.set(dataKey, itemToDataWrapper((Item) value));
            } else {
                wrapper.set(dataKey, value);
            }
        });
        return wrapper;
    }
}
