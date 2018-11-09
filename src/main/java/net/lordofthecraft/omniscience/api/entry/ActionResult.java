package net.lordofthecraft.omniscience.api.entry;

import com.mongodb.lang.Nullable;

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

    public static ActionResult failure(String reason) {
        return new ActionResult(reason, Status.FAILURE);
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
