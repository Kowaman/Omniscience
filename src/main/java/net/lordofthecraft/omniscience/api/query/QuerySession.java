package net.lordofthecraft.omniscience.api.query;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.entry.EntryMapper;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class QuerySession {

    private final List<DataEntry> dataEntries = Lists.newArrayList();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private int pageSize = 10;

    /**
     * Gets the page of the last run query. This is A BLOCKING METHOD. Do <b>NOT</b> call on the main thread!
     *
     * @param page The page to fetch
     * @return The list of data entries of the page
     */
    private List<DataEntry> getPage(int page) {
        List<DataEntry> pageEntries = Lists.newArrayList();
        lock.readLock().lock();
        try {
            for (int i = page * pageSize; i < (page * pageSize) + pageSize; i++) {
                pageEntries.add(dataEntries.get(i));
            }
        } finally {
            lock.readLock().unlock();
        }
        return pageEntries;
    }

    public void displayPage(CommandSender sender, int page) {
        List<DataEntry> pageContents = getPage(page);

    }

    public void setPageSize(int size) {
        this.pageSize = size;
    }

    void runQuery(CommandSender sender, Query query) {
        MongoConnectionHandler connectionHandler = MongoConnectionHandler.getInstance();
        MongoCollection<Document> entries = connectionHandler.getDataCollection();
        MongoCursor<Optional<DataEntry>> cursor = entries.find(query.build())
                .map(EntryMapper.INSTANCE::mapDocumentToDataEntry).iterator();
        //Clear the current session, if it exists, and try to take control of the lock
        lock.writeLock().lock();
        try {
            dataEntries.clear();
            while (cursor.hasNext()) {
                cursor.next().ifPresent(dataEntries::add);
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Something has gone wrong while performing this search.");
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
        displayPage(sender, 0);
    }
}
