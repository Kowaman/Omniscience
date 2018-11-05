package net.lordofthecraft.omniscience.consumer;

import com.google.common.collect.Lists;
import com.mongodb.async.client.MongoCollection;
import net.lordofthecraft.omniscience.domain.DataEntry;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EntryConsumer implements Runnable {

    private final MongoConnectionHandler connectionHandler;
    private final ReentrantLock lock;
    private final Logger logger;
    private List<DataEntry> dataEntryList;

    public EntryConsumer(MongoConnectionHandler connectionHandler, Logger logger) {
        this.connectionHandler = connectionHandler;
        this.logger = logger;
        this.dataEntryList = Lists.newArrayList();
        this.lock = new ReentrantLock();
    }

    public void addDataEntry(DataEntry entry) {
        this.dataEntryList.add(entry);
    }

    @Override
    public void run() {
        if (lock.tryLock()) {
            try {
                List<DataEntry> entries = new ArrayList<>(dataEntryList);
                this.dataEntryList.clear();
                MongoCollection<Document> dataEntryCollection = connectionHandler.getDataCollection();
                dataEntryCollection.insertMany(entries.stream().map(DataEntry::asDocument).collect(Collectors.toList()), (result, t) -> {
                    if (t != null) {
                        logger.log(Level.SEVERE, "Failed to save " + entries.size() + " records! Putting them back to try to save. See the error for details!", t);
                        dataEntryList.addAll(entries);
                    } else {
                        logger.log(Level.INFO, "Successfully saved " + dataEntryList.size() + " records.");
                    }
                });
            } finally {
                lock.unlock();
            }
        }

    }
}
