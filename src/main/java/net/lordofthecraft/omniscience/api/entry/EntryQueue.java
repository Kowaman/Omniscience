package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.api.data.DataWrapper;

import java.util.concurrent.LinkedBlockingDeque;

public final class EntryQueue {

    private static final LinkedBlockingDeque<DataWrapper> queue = new LinkedBlockingDeque<>();

    private EntryQueue() {
    }

    public static void submit(final DataWrapper wrapper) {
        if (wrapper == null) {
            throw new IllegalArgumentException("A null wrapper was handed to save for the saving queue");
        }

        queue.add(wrapper);
    }

    public static LinkedBlockingDeque<DataWrapper> getQueue() {
        return queue;
    }
}
