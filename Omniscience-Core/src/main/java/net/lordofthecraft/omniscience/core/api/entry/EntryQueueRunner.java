package net.lordofthecraft.omniscience.core.api.entry;

import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.api.entry.EntryQueue;
import net.lordofthecraft.omniscience.core.Omniscience;

import java.util.List;

public final class EntryQueueRunner implements Runnable {

    @Override
    public void run() {
        List<DataWrapper> batchWrappers = Lists.newArrayList();

        while (!EntryQueue.getQueue().isEmpty()) {
            DataWrapper wrapper = EntryQueue.getQueue().poll();
            if (wrapper != null) {
                Omniscience.logDebug("We're now saving the event: " + wrapper.getString(DataKeys.EVENT_NAME).orElse("Unknown"));
                Omniscience.logDebug("Data: " + wrapper);
                batchWrappers.add(wrapper);
            }
        }

        if (batchWrappers.size() > 0) {
            try {
                Omniscience.getStorageHandler().records().write(batchWrappers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
