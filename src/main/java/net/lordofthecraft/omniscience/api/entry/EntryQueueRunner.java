package net.lordofthecraft.omniscience.api.entry;

import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;

import java.util.List;

public final class EntryQueueRunner implements Runnable {

    @Override
    public void run() {
        List<DataWrapper> batchWrappers = Lists.newArrayList();

        while (!EntryQueue.getQueue().isEmpty()) {
            DataWrapper wrapper = EntryQueue.getQueue().poll();
            if (wrapper != null) {
                batchWrappers.add(wrapper);
            }
        }

        if (batchWrappers.size() > 0) {
            try {
                MongoConnectionHandler.getInstance().write(batchWrappers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
