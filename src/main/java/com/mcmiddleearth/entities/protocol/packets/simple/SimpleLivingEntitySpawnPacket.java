package com.mcmiddleearth.entities.protocol.packets.simple;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.protocol.packets.AbstractPacket;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SimpleLivingEntitySpawnPacket extends AbstractPacket {

    private final PacketContainer spawn;

    private final McmeEntity entity;

    public SimpleLivingEntitySpawnPacket(McmeEntity entity) {
        this.entity = entity;
        spawn = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        spawn.getIntegers().write(0, entity.getEntityId())
                .write(1, getEntityType(entity.getMcmeEntityType().getBukkitEntityType()));
        //spawn.getEntityTypeModifier().write(0,entity.getType().getBukkitEntityType());
        spawn.getUUIDs().write(0, entity.getUniqueId());
        update();
    }

    @Override
    public void update() {
        Location loc = entity.getLocation();
//Logger.getGlobal().info("Spawn living: "+loc.getYaw()+" "+loc.getPitch());
        spawn.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        spawn.getBytes()
                .write(0, (byte) (loc.getPitch()*256/360))
                .write(1, (byte) (loc.getPitch()*256/360))
                .write(2,(byte) (loc.getYaw()*256/360));
    }

    @Override
    public void send(Player recipient) {
//Logger.getGlobal().info("send living spawn to : "+recipient.getName());
        send(spawn, recipient);
    }

    private int getEntityType(EntityType entityType) {
        return switch (entityType) {
            case AXOLOTL -> 3;
            case BAT -> 4;
            case BEE -> 5;
            case BLAZE -> 6;
            case CAT -> 8;
            case CAVE_SPIDER -> 9;
            case CHICKEN -> 10;
            case COD -> 11;
            case COW -> 12;
            case CREEPER -> 13;
            case DOLPHIN -> 14;
            case DONKEY -> 15;
            case DROWNED -> 17;
            case ELDER_GUARDIAN -> 18;
            case ENDER_DRAGON -> 20;
            case ENDERMAN -> 21;
            case ENDERMITE -> 22;
            case EVOKER -> 23;
            case EVOKER_FANGS -> 24;
            case FOX -> 29;
            case GHAST -> 30;
            case GIANT -> 31;
            case GLOW_SQUID -> 33;
            case GOAT -> 34;
            case GUARDIAN -> 35;
            case HOGLIN -> 36;
            case HORSE -> 37;
            case HUSK -> 38;
            case ILLUSIONER -> 39;
            case IRON_GOLEM -> 40;
            case DROPPED_ITEM -> 41;
            case LLAMA -> 46;
            case MAGMA_CUBE -> 48;
            case MULE -> 57;
            case MUSHROOM_COW -> 58;
            case OCELOT -> 59;
            case PANDA -> 61;
            case PARROT -> 62;
            case PHANTOM -> 63;
            case PIG -> 64;
            case PIGLIN -> 65;
            case PIGLIN_BRUTE -> 66;
            case PILLAGER -> 67;
            case POLAR_BEAR -> 68;
            case PUFFERFISH -> 70;
            case RABBIT -> 71;
            case RAVAGER -> 72;
            case SALMON -> 73;
            case SHEEP -> 74;
            case SHULKER -> 75;
            case SILVERFISH -> 77;
            case SKELETON -> 78;
            case SKELETON_HORSE -> 79;
            case SLIME -> 80;
            case SNOWMAN -> 82;
            case SPIDER -> 85;
            case SQUID -> 86;
            case STRAY -> 87;
            case STRIDER -> 88;
            case TRADER_LLAMA -> 94;
            case TROPICAL_FISH -> 95;
            case TURTLE -> 96;
            case VEX -> 97;
            case VILLAGER -> 98;
            case VINDICATOR -> 99;
            case WANDERING_TRADER -> 100;
            case WITCH -> 101;
            case WITHER -> 102;
            case WITHER_SKELETON -> 103;
            case WOLF -> 105;
            case ZOGLIN -> 106;
            case ZOMBIE -> 107;
            case ZOMBIE_HORSE -> 108;
            case ZOMBIE_VILLAGER -> 109;
            case ZOMBIFIED_PIGLIN -> 110;
            default -> //villager
                    93;
        };
    }
}
