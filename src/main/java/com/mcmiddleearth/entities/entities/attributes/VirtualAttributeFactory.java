package com.mcmiddleearth.entities.entities.attributes;

import com.mcmiddleearth.entities.entities.McmeEntityType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VirtualAttributeFactory {

    public static Map<Attribute, AttributeInstance> getAttributesFor(McmeEntityType type) {
        Map<Attribute, AttributeInstance> attributes = new HashMap<>();
        if(type.isCustomType()) {
            Arrays.stream(Attribute.values()).forEach(attribute -> attributes.put(attribute, getAttributeInstance(attribute, null)));
        } else {
            switch(type.getBukkitEntityType()) {
                case PLAYER:
                    attributes.put(Attribute.GENERIC_ATTACK_SPEED,getAttributeInstance(Attribute.GENERIC_ATTACK_SPEED, 4.0));
                    attributes.put(Attribute.GENERIC_LUCK,getAttributeInstance(Attribute.GENERIC_LUCK, 0.0)); break;
                case HORSE:
                    attributes.put(Attribute.HORSE_JUMP_STRENGTH,getAttributeInstance(Attribute.HORSE_JUMP_STRENGTH, 0.7)); break;
                case BEE:
                    attributes.put(Attribute.GENERIC_FLYING_SPEED,getAttributeInstance(Attribute.GENERIC_FLYING_SPEED, 0.6)); break;
                case PARROT:
                    attributes.put(Attribute.GENERIC_FLYING_SPEED,getAttributeInstance(Attribute.GENERIC_FLYING_SPEED, 0.4)); break;
                case ZOMBIE:
                    attributes.put(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS,getAttributeInstance(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0)); break;
            }
            if(type.getBukkitEntityType().equals(EntityType.ZOMBIE)) {
                attributes.put(Attribute.GENERIC_FOLLOW_RANGE,getAttributeInstance(Attribute.GENERIC_FOLLOW_RANGE, 40.0));
            } else {
                attributes.put(Attribute.GENERIC_FOLLOW_RANGE,getAttributeInstance(Attribute.GENERIC_FOLLOW_RANGE, 16.0));
            }
            attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,getAttributeInstance(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.0));
            attributes.put(Attribute.GENERIC_MAX_HEALTH,getAttributeInstance(Attribute.GENERIC_MAX_HEALTH, getMaxHealth(type.getBukkitEntityType())));
            attributes.put(Attribute.GENERIC_MOVEMENT_SPEED,getAttributeInstance(Attribute.GENERIC_MOVEMENT_SPEED, getSpeed(type.getBukkitEntityType())));
            attributes.put(Attribute.GENERIC_ARMOR,getAttributeInstance(Attribute.GENERIC_ARMOR, 0.0));
            attributes.put(Attribute.GENERIC_ARMOR_TOUGHNESS,getAttributeInstance(Attribute.GENERIC_ARMOR_TOUGHNESS, 0.0));
            switch(type.getBukkitEntityType()) {
                case RAVAGER:
                    attributes.put(Attribute.GENERIC_ATTACK_KNOCKBACK,getAttributeInstance(Attribute.GENERIC_ATTACK_KNOCKBACK, 1.5)); break;
                case HOGLIN:
                case ZOGLIN:
                    attributes.put(Attribute.GENERIC_ATTACK_KNOCKBACK,getAttributeInstance(Attribute.GENERIC_ATTACK_KNOCKBACK, 1.0)); break;
                default:
                    attributes.put(Attribute.GENERIC_ATTACK_KNOCKBACK,getAttributeInstance(Attribute.GENERIC_ATTACK_KNOCKBACK, 0.0));
            }
            attributes.put(Attribute.GENERIC_FOLLOW_RANGE,getAttributeInstance(Attribute.GENERIC_FOLLOW_RANGE, 40.0));
        }
        return attributes;
    }

    public static VirtualEntityAttributeInstance getAttributeInstance(Attribute attribute, Double baseValue) {
        double defaultValue = 0;
        switch(attribute) {
            case GENERIC_MAX_HEALTH:
                defaultValue = 20;
            case GENERIC_FOLLOW_RANGE:
                defaultValue = 32;
            case GENERIC_KNOCKBACK_RESISTANCE:
            case GENERIC_ATTACK_KNOCKBACK:
            case GENERIC_ARMOR:
            case GENERIC_ARMOR_TOUGHNESS:
            case GENERIC_LUCK:
            case ZOMBIE_SPAWN_REINFORCEMENTS:
                defaultValue = 0;
            case GENERIC_MOVEMENT_SPEED:
            case HORSE_JUMP_STRENGTH:
                defaultValue = 0.7;
            case GENERIC_FLYING_SPEED:
                defaultValue = 0.4;
            case GENERIC_ATTACK_DAMAGE:
                defaultValue = 2;
            case GENERIC_ATTACK_SPEED:
                defaultValue = 4;
        }
        if(baseValue == null) {
            baseValue = defaultValue;
        }
        return new VirtualEntityAttributeInstance(attribute, baseValue, defaultValue);
    }

    public static double getSpeed(EntityType type) {
        switch(type) {
            case PLAYER: return 0.1;
            case PANDA: return 0.15;
            case HORSE: return new Random().nextDouble()*0.2250+0.1125;
            case DONKEY:
            case LLAMA:
            case MULE:
            case STRIDER: return 0.175;
            case SLIME:
            case COW:
            case MAGMA_CUBE:
            case PARROT:
            case SKELETON_HORSE:
            case SNOWMAN:
            case ZOMBIE_HORSE:
            case MUSHROOM_COW: return 0.2;
            case BLAZE:
            case DROWNED:
            case HUSK:
            case SHEEP:
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case ZOMBIFIED_PIGLIN: return 0.23;
            case CHICKEN:
            case CREEPER:
            case ENDERMITE:
            case IRON_GOLEM:
            case PIG:
            case POLAR_BEAR:
            case SILVERFISH:
            case SKELETON:
            case STRAY:
            case TURTLE:
            case WITCH:
            case WITHER_SKELETON: return 0.25;
            case BEE:
            case CAT:
            case CAVE_SPIDER:
            case ELDER_GUARDIAN:
            case FOX:
            case OCELOT:
            case RABBIT:
            case RAVAGER:
            case SPIDER:
            case WOLF: return 0.3;
            case PILLAGER:
            case VINDICATOR: return 0.35;
            case HOGLIN: return 0.4;
            case EVOKER:
            case GIANT:
            case GUARDIAN:
            case ILLUSIONER:
            case PIGLIN:
            case VILLAGER:
            case WANDERING_TRADER: return 0.5;
            case WITHER: return 0.6;
            case BAT:
            case COD:
            case ENDER_DRAGON:
            case GHAST:
            case PUFFERFISH:
            case SALMON:
            case SHULKER:
            case SQUID:
            case TROPICAL_FISH:
            case VEX: return 0.7;
            case DOLPHIN: return 1.2;
            default: return 0;
        }
    }

    public static double getMaxHealth(EntityType type) {
        switch (type) {
            case COD:
            case TROPICAL_FISH:
            case SALMON: return 3;
            case SNOWMAN:
            case CHICKEN: return 4;
            case BAT:
            case PARROT: return 6;
            case SHEEP: return 8;
            case GHAST:
            case COW:
            case DOLPHIN:
            case FOX:
            case OCELOT:
            case MUSHROOM_COW:
            case SQUID:
            case CAT: return 10;
            case VEX: return 14;
            case SKELETON_HORSE: return 15;
            case DONKEY:
            case MULE:
            case HORSE:
            case TRADER_LLAMA: return new Random().nextInt()*16+15;
            case SPIDER:
            case MAGMA_CUBE:
            case PIGLIN: return 16;
            case CREEPER:
            case DROWNED:
            case STRIDER:
            case VILLAGER:
            case WANDERING_TRADER:
            case PANDA:
            case ZOMBIFIED_PIGLIN:
            case HUSK:
            case PHANTOM:
            case SKELETON:
            case WITHER_SKELETON:
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case PLAYER: return 20;
            case EVOKER:
            case VINDICATOR: return 24;
            case WITCH:
            case PILLAGER: return 26;
            case POLAR_BEAR:
            case GUARDIAN:
            case SHULKER:
            case TURTLE: return 30;
            case ENDERMAN:
            case HOGLIN:
            case ZOGLIN: return 40;
            case PIGLIN_BRUTE: return 50;
            case RAVAGER:
            case IRON_GOLEM: return 100;
            case ENDER_DRAGON: return 200;
            case WITHER: return 300;
            default: return 20;
        }
    }
}
