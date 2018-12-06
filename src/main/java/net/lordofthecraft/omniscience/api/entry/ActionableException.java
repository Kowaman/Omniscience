package net.lordofthecraft.omniscience.api.entry;

public class ActionableException extends Exception {

    private final ActionResult result;

    public ActionableException(ActionResult result) {
        super(result.getResult());
        this.result = result;
    }

    public ActionResult getResult() {
        return result;
    }
}
