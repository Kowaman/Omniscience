package net.lordofthecraft.omniscience.domain;

import org.bson.Document;

import java.util.Date;
import java.util.UUID;

public abstract class BaseEntry implements DataEntry {

    private final Actor actor;
    private final Date date;
    private final UUID world;
    private final int x, y, z;

    public BaseEntry(int x, int y, int z, UUID world, Actor actor, Date date) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.actor = actor;
        this.date = date;
    }

    protected abstract void saveAdditionsToDocument(Document document);

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public UUID getWorld() {
        return world;
    }

    @Override
    public Date getTime() {
        return date;
    }

    @Override
    public Actor getActor() {
        return actor;
    }

    @Override
    public Document asDocument() {
        Document document = new Document();
        document
                .append("x", x)
                .append("y", y)
                .append("z", z)
                .append("world", world)
                .append("actor", actor.getKey())
                .append("date", date)
                .append("type", getDiscriminator());
        saveAdditionsToDocument(document);
        return document;
    }
}
