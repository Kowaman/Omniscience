package net.lordofthecraft.omniscience.api.entry;

public class ItemDepositEntry extends DataItemEntryComplete implements Actionable {

    @Override
    public ActionResult rollback() throws Exception {
        return rollbackEntry(true);
    }

    @Override
    public ActionResult restore() throws Exception {
        return rollbackEntry(false);
    }
}
