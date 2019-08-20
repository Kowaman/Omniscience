package net.lordofthecraft.omniscience.api;

import net.lordofthecraft.omniscience.api.interfaces.IOmniscience;

public final class OmniscienceProvider {
    static IOmniscience INSTANCE = null;

    private OmniscienceProvider() {
        throw new UnsupportedOperationException();
    }

    public static void init(IOmniscience theOneWhoSeesAll) {
        if (INSTANCE != null) throw new IllegalStateException("Omniscience was already initialized");
        INSTANCE = theOneWhoSeesAll;
    }

}
