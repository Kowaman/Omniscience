package net.lordofthecraft.omniscience.api.query;

import org.bukkit.command.CommandSender;

public class QueryRunner implements Runnable {

    private final CommandSender sender;
    private final QuerySession session;
    private final Query toRun;

    public QueryRunner(CommandSender sender, QuerySession session, Query toRun) {
        this.sender = sender;
        this.session = session;
        this.toRun = toRun;
    }

    @Override
    public void run() {
        session.runQuery(sender, toRun);
    }
}
