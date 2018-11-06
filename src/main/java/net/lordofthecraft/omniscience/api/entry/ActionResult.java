package net.lordofthecraft.omniscience.api.entry;

import jdk.internal.jline.internal.Nullable;

public class ActionResult {

    private final String result;
    private final Status status;

    private ActionResult(@Nullable String result, Status status) {
        this.result = result;
        this.status = status;
    }

    public static ActionResult success() {
        return new ActionResult(null, Status.SUCCESS);
    }

    public String getResult() {
        return result;
    }

    public Status getStatus() {
        return status;
    }

    enum Status {
        SUCCESS,
        FAILURE
    }
}
