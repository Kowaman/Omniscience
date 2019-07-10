// This script will move the relevent data fields to a "data" object, instead of letting them float around pointlessly
db.DataEntry.find().forEach(function(document) {
    const original_block = document.OriginalBlock || undefined;
    const new_block = document.NewBlock || undefined;
    const entity = document.Entity || undefined;
    const sign_text = document.SignText || undefined;
    const inventory = document.Inventory || undefined;
    const before = document.Before || undefined;
    const after = document.After || undefined;
    const record = document.Record || undefined;
    const teleportCause = document.TeleportCause || undefined;
    const itemstack = document.ItemStack || undefined;
    const message = document.Message || undefined;
    const itemSlot = document.ItemSlot || undefined;
    const bannerPatterns = document.BannerPatterns || undefined;
    const damageCause = document.DamageCause || undefined;
    const damageAmount = document.DamageAmount || undefined;

    const newData = {}
    if (original_block) {
        newData.OriginalBlock = original_block;
    }
    if (new_block) {
        newData.NewBlock = new_block;
    }
    if (entity) {
        newData.Entity = entity;
    }
    if (sign_text) {
        newData.SignText = sign_text;
    }
    if (inventory) {
        newData.Inventory = inventory;
    }
    if (before) {
        newData.Before = before;
    }
    if (after) {
        newData.After = after;
    }
    if (record) {
        newData.Record = record;
    }
    if (teleportCause) {
        newData.TeleportCause = teleportCause;
    }
    if (itemstack) {
        newData.ItemStack = itemstack;
    }
    if (message) {
        newData.Message = message;
    }
    if (itemSlot) {
        newData.ItemSlot = itemSlot;
    }
    if (bannerPatterns) {
        newData.BannerPatterns = bannerPatterns;
    }
    if (damageCause) {
        newData.DamageCause = damageCause;
    }
    if (damageAmount) {
        newData.DamageAmount = damageAmount;
    }

    db.DataEntry.update(
        { _id: document._id },
        {
            $set: {
                data: newData
            },
            $unset: {
                OriginalBlock: '',
                NewBlock: '',
                Entity: '',
                SignText: '',
                Inventory: '',
                Before: '',
                After: '',
                Record: '',
                TeleportCause: '',
                ItemStack: '',
                Message: '',
                ItemSlot: '',
                BannerPatterns: '',
                DamageCause: '',
                DamageAmount: ''
            }
        }
    )
});