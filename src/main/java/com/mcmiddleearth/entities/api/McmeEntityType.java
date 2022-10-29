package com.mcmiddleearth.entities.api;

import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class provides information about which type of entity should be created in the spawning process.
 *
 *
 */
public class McmeEntityType {

    private boolean isCustomType;

    private CustomEntityType customType;

    private EntityType bukkitEntityType;

    public static enum CustomEntityType {
        BAKED_ANIMATION,
        WINGED_FLIGHT,
        SPEECH_BALLOON,
        BONE, TWO_AXIS_ROTATION;
    }

    /**
     * BAKED_ANIMATION represents an animated entity with animations provided by Animated Java. It requires a
     * data file set in the factory that must be placed in animation folder of the plugin.
     *
     * SPEECH_BALLOON and BONE are for internal use only and should not be spawned by external plugins.
     * @param customType custom type to spawn for now BAKED_ANIMATION only.
     */
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

    /**
     * Most bukkit entity types can be used here. Some types that require special spawning process might fail.
     * @param bukkitEntityType bukkit entity type to spawn
     */
    public McmeEntityType(EntityType bukkitEntityType) {
        this.bukkitEntityType = bukkitEntityType;
        isCustomType = false;
    }

    /**
     * Tries to create a matching entity type from a string input.
     * @param type Text to parse
     */
    public static McmeEntityType valueOf(String type) {
    //public McmeEntityType(String type) {
        if(type == null || type.equals(""))  return null;
        McmeEntityType entityType = new McmeEntityType(EntityType.BAT);
        entityType.bukkitEntityType = null;
        try {
            entityType.bukkitEntityType = EntityType.valueOf(type.toUpperCase());
        } catch(Exception ignore) {}
        if(entityType.bukkitEntityType==null) {
            CustomEntityType customType = null;
            try {
                customType = CustomEntityType.valueOf(type.toUpperCase());
                entityType.customType = customType;
                if(customType.equals(CustomEntityType.BONE)) {
                    entityType.bukkitEntityType = EntityType.ARMOR_STAND;
                }
            } catch(Exception exception) {
                return null;
            }
            entityType.isCustomType = true;
        } else {
            entityType.isCustomType = false;
        }
        return entityType;
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

    public String name() {
        if(isCustomType) {
            return customType.name().toLowerCase();
        } else {
            return bukkitEntityType.name().toLowerCase();
        }
    }

    public boolean isProjectile() {
        return bukkitEntityType!=null && (bukkitEntityType.equals(EntityType.ARROW)
                                            || bukkitEntityType.equals(EntityType.LLAMA_SPIT)
                                            || bukkitEntityType.equals(EntityType.SNOWBALL)
                                            || bukkitEntityType.equals(EntityType.SPECTRAL_ARROW)
                                            || bukkitEntityType.equals(EntityType.EGG)
                                            || bukkitEntityType.equals(EntityType.THROWN_EXP_BOTTLE)
                                            || bukkitEntityType.equals(EntityType.SPLASH_POTION)
                                            || bukkitEntityType.equals(EntityType.TRIDENT)
                                            || bukkitEntityType.equals(EntityType.ENDER_PEARL));
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

    public static Collection<String> availableTypes() {
        Collection<String> result = Arrays.stream(EntityType.values())
                .map(type -> type.name().toLowerCase()).collect(Collectors.toList());
        result.addAll(Arrays.stream(CustomEntityType.values())
                .map(type -> type.name().toLowerCase()).collect(Collectors.toList()));
        return result.stream().sorted().collect(Collectors.toList());
    }
}
