package net.lordofthecraft.omniscience.api.entry;

public interface Actionable {

    ActionResult rollback() throws Exception;
}
