package net.lordofthecraft.omniscience.api.query;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class QuerySession {

    private final List<DataEntry> dataEntries = Lists.newArrayList();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private int pageSize = 10;

    public List<DataEntry> getPage(int page) {
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

    public void setPageSize(int size) {
        this.pageSize = size;
    }

    private void runQuery(Query query) {
        //Clear the current session, if it exists, and try to take control of the lock
        lock.writeLock().lock();
        try {
            dataEntries.clear();
        } finally {
            lock.writeLock().unlock();
        }
        MongoConnectionHandler connectionHandler = MongoConnectionHandler.getInstance();
        MongoCollection<Document> entries = connectionHandler.getDataCollection();
        entries.find(query.build())
                .map(document -> {
                    return null;
                })
                .forEach((Consumer<? super Object>) Document -> {
                    lock.writeLock().lock();
                    try {

                    } finally {
                        lock.writeLock().unlock();
                    }
                });
    }
}
