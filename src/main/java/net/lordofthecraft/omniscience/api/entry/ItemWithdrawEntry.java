package net.lordofthecraft.omniscience.api.entry;

public class ItemWithdrawEntry extends DataItemEntryComplete implements Actionable {

    @Override
    public ActionResult rollback() throws Exception {
        return rollbackEntry(false);
    }

    @Override
    public ActionResult restore() throws Exception {
        return rollbackEntry(true);
    }
}
