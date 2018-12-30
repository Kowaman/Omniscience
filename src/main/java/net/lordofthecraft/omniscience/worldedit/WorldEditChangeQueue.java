package net.lordofthecraft.omniscience.worldedit;

import java.util.concurrent.LinkedBlockingDeque;

public final class WorldEditChangeQueue {

    private static final LinkedBlockingDeque<ChangeWrapper> queue = new LinkedBlockingDeque<>();

    private WorldEditChangeQueue() {

    }

    public static void submit(ChangeWrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        queue.add(wrapper);
    }

    public static LinkedBlockingDeque<ChangeWrapper> getQueue() {
        return queue;
    }
}
