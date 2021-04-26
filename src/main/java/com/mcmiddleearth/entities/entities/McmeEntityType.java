package com.mcmiddleearth.entities.entities;

import org.bukkit.entity.EntityType;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class McmeEntityType {

    private final boolean isCustomType;

    private CustomEntityType customType;

    private EntityType bukkitEntityType;

    public static enum CustomEntityType {
        BAKED_ANIMATION,
        BONE;
    }

    public McmeEntityType(CustomEntityType customType) {
        setCustom(customType);
        isCustomType = true;
    }

    private void setCustom(CustomEntityType customType) {
        this.customType = customType;
        if(customType.equals(CustomEntityType.BONE)) {
            bukkitEntityType = EntityType.ARMOR_STAND;
        }
    }

    public McmeEntityType(EntityType bukkitEntityType) {
        this.bukkitEntityType = bukkitEntityType;
        isCustomType = false;
    }

    public McmeEntityType(String type) {
        bukkitEntityType = null;
        try {
            bukkitEntityType = EntityType.valueOf(type.toUpperCase());
        } catch(Exception ignore) {}
        if(bukkitEntityType==null) {
            CustomEntityType customType = null;
            try {
                customType = CustomEntityType.valueOf(type.toUpperCase());
                setCustom(customType);
            } catch(Exception exception) {
                Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Invalid entity type!", exception);
            }
            isCustomType = true;
        } else {
            this.isCustomType = false;
        }
    }

    public boolean isCustomType() {
        return isCustomType;
    }

    public CustomEntityType getCustomType() {
        return customType;
    }

    public EntityType getBukkitEntityType() {
        return bukkitEntityType;
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof McmeEntityType) && this.isCustomType == ((McmeEntityType) other).isCustomType) {
            if(this.isCustomType) {
                return this.getCustomType().equals(((McmeEntityType) other).getCustomType());
            } else {
                return this.getBukkitEntityType().equals(((McmeEntityType) other).bukkitEntityType);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCustomType, customType, bukkitEntityType);
    }

}
