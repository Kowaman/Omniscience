package net.lordofthecraft.omniscience;

import net.lordofthecraft.omniscience.io.StorageHandler;
import net.lordofthecraft.omniscience.io.dynamo.DynamoStorageHandler;
import net.lordofthecraft.omniscience.io.mongo.MongoStorageHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public enum OmniConfig {
    INSTANCE;

    private String databaseName;
    private DatabaseType dbType;

    private boolean defaultsEnabled;
    private int defaultRadius;
    private String defaultSearchTime;
    private int radiusLimit;
    private int lookupSizeLimit;
    private String dateFormat;
    private ChatColor primary = ChatColor.AQUA;
    private int actionablesLimit;
    private String recordExpiry;
    private int maxPoolSize;
    private int minPoolSize;
    private int purgeBatchLimit;
    private ChatColor secondary = ChatColor.GREEN;
    private String simpleDateFormat;
    private String tableName;

    void setup(FileConfiguration configuration) {
        if (dbType == null) {
            dbType = DatabaseType.valueOf(configuration.getString("database.type").toUpperCase());
        }
        this.databaseName = configuration.getString("database.name");
        this.tableName = configuration.getString("database.dataTableName");
        this.defaultsEnabled = configuration.getBoolean("defaults.enabled");
        this.defaultRadius = configuration.getInt("defaults.radius");
        this.defaultSearchTime = configuration.getString("defaults.time");
        this.radiusLimit = configuration.getInt("limits.radius");
        this.lookupSizeLimit = configuration.getInt("limits.lookup.size");
        this.actionablesLimit = configuration.getInt("limits.actionables");
        this.dateFormat = configuration.getString("display.format");
        this.simpleDateFormat = configuration.getString("display.simpleFormat");
        this.recordExpiry = configuration.getString("storage.expireRecords");
        this.maxPoolSize = configuration.getInt("storage.maxPoolSize");
        this.minPoolSize = configuration.getInt("storage.minPoolSize");
        this.purgeBatchLimit = configuration.getInt("storage.purgeBatchLimit");
        String wandMaterialName = configuration.getString("wand.material");

        wandMaterial = Material.matchMaterial(wandMaterialName);
        if (wandMaterial == null || !wandMaterial.isBlock()) {
            wandMaterial = Material.REDSTONE_LAMP;
            Omniscience.getPluginInstance().getLogger().warning("Invalid configuration option for wand.material: " + wandMaterialName + ". Defaulting to REDSTONE_LAMP");
        }


    }

    private Material wandMaterial;

    public DatabaseType getDbType() {
        return dbType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public ChatColor getPrimary() {
        return primary;
    }

    public void setPrimary(ChatColor primary) {
        this.primary = primary;
    }

    public ChatColor getSecondary() {
        return secondary;
    }

    public void setSecondary(ChatColor secondary) {
        this.secondary = secondary;
    }

    public boolean areDefaultsEnabled() {
        return defaultsEnabled;
    }

    public String getSimpleDateFormat() {
        return simpleDateFormat;
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

    enum DatabaseType {
        MONGODB(MongoStorageHandler.class),
        DYNAMODB(DynamoStorageHandler.class);

        Class<? extends StorageHandler> storageClass;

        DatabaseType(Class<? extends StorageHandler> storageClass) {
            this.storageClass = storageClass;
        }

        public Class<? extends StorageHandler> getStorageClass() {
            return storageClass;
        }

        public StorageHandler invokeConstructor() throws Exception {
            return storageClass.getConstructor().newInstance();
        }
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

    public Material getWandMaterial() {
        return wandMaterial;
    }
}
