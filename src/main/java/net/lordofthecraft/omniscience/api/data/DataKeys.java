package net.lordofthecraft.omniscience.api.data;

public final class DataKeys {

    public static final DataKey LOCATION = DataKey.of("Location");
    public static final DataKey WORLD = DataKey.of("World");
    public static final DataKey X = DataKey.of("X");
    public static final DataKey Y = DataKey.of("Y");
    public static final DataKey Z = DataKey.of("Z");
    public static final DataKey EVENT_NAME = DataKey.of("Event");
    public static final DataKey PLAYER_ID = DataKey.of("Player");
    public static final DataKey CAUSE = DataKey.of("Cause");
    public static final DataKey TARGET = DataKey.of("Target");
    public static final DataKey COUNT = DataKey.of("Count");
    public static final DataKey CREATED = DataKey.of("Created");
    public static final DataKey BLOCK_DATA = DataKey.of("BlockData");
    public static final DataKey MATERIAL_TYPE = DataKey.of("MaterialType");
    public static final DataKey ENTITY_TYPE = DataKey.of("EntityType");
    public static final DataKey ITEMSTACK = DataKey.of("ItemStack");
    public static final DataKey ORIGINAL_BLOCK = DataKey.of("OriginalBlock");
    public static final DataKey NEW_BLOCK = DataKey.of("NewBlock");
    public static final DataKey IPADDRESS = DataKey.of("IpAddress");
    public static final DataKey QUANTITY = DataKey.of("Quantity");
    public static final DataKey MESSAGE = DataKey.of("Message");

    //META TAGS
    public static final DataKey DISPLAY_METHOD = DataKey.of("DisplayMethod");

    private DataKeys() {
    }
}