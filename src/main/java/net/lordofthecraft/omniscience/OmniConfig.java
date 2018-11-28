package net.lordofthecraft.omniscience;

import org.bukkit.configuration.file.FileConfiguration;

public enum OmniConfig {
    INSTANCE;

    private boolean defaultsEnabled;
    private int defaultRadius;
    private String defaultSearchTime;
    private int radiusLimit;
    private int lookupSizeLimit;
    private String dateFormat;
    private int actionablesLimit;
    private String recordExpiry;
    private int maxPoolSize;
    private int minPoolSize;
    private int purgeBatchLimit;

    void setup(FileConfiguration configuration) {
        this.defaultsEnabled = configuration.getBoolean("defaults.enabled");
        this.defaultRadius = configuration.getInt("defaults.radius");
        this.defaultSearchTime = configuration.getString("defaults.time");
        this.radiusLimit = configuration.getInt("limits.radius");
        this.lookupSizeLimit = configuration.getInt("limits.lookup.size");
        this.actionablesLimit = configuration.getInt("limits.actionables");
        this.dateFormat = configuration.getString("display.format");
        this.recordExpiry = configuration.getString("storage.expireRecords");
        this.maxPoolSize = configuration.getInt("storage.maxPoolSize");
        this.minPoolSize = configuration.getInt("storage.minPoolSize");
        this.purgeBatchLimit = configuration.getInt("storage.purgeBatchLimit");
    }

    public boolean isDefaultsEnabled() {
        return defaultsEnabled;
    }

    public int getDefaultRadius() {
        return defaultRadius;
    }

    public String getDefaultSearchTime() {
        return defaultSearchTime;
    }

    public int getRadiusLimit() {
        return radiusLimit;
    }

    public int getLookupSizeLimit() {
        return lookupSizeLimit;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public int getActionablesLimit() {
        return actionablesLimit;
    }

    public String getRecordExpiry() {
        return recordExpiry;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public int getPurgeBatchLimit() {
        return purgeBatchLimit;
    }
}
