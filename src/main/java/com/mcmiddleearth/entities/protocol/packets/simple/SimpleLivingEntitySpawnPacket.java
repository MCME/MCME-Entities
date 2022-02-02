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
        switch(entityType) {
            case BAT: return 3;
            case BEE: return 4;
            case BLAZE: return 5;
            case CAT: return 7;
            case CAVE_SPIDER: return 8;
            case CHICKEN: return 9;
            case COD: return 10;
            case COW: return 11;
            case CREEPER: return 12;
            case DOLPHIN: return 13;
            case DONKEY: return 14;
            case DROWNED: return 16;
            case ELDER_GUARDIAN: return 17;
            case ENDER_DRAGON: return 19;
            case ENDERMAN: return 20;
            case ENDERMITE: return 21;
            case EVOKER: return 22;
            case EVOKER_FANGS: return 23;
            case FOX: return 28;
            case GHAST: return 29;
            case GIANT: return 30;
            case GUARDIAN: return 31;
            case HOGLIN: return 32;
            case HORSE: return  33;
            case HUSK: return 34;
            case ILLUSIONER: return 35;
            case IRON_GOLEM: return 36;
            case DROPPED_ITEM: return 37;
            case LLAMA: return 42;
            case MAGMA_CUBE: return 44;
            case MULE: return  52;
            case MUSHROOM_COW: return 53;
            case OCELOT: return  54;
            case PANDA: return 56;
            case PARROT: return 57;
            case PHANTOM: return 58;
            case PIG: return 59;
            case PIGLIN: return 60;
            case PIGLIN_BRUTE: return 61;
            case PILLAGER: return 62;
            case POLAR_BEAR: return 63;
            case PUFFERFISH: return 65;
            case RABBIT: return 66;
            case RAVAGER: return 67;
            case SALMON: return 68;
            case SHEEP: return 69;
            case SHULKER: return 70;
            case SILVERFISH: return 72;
            case SKELETON: return 73;
            case SKELETON_HORSE: return 74;
            case SLIME: return 75;
            case SNOWMAN: return 77;
            case SPIDER: return 80;
            case SQUID: return 81;
            case STRAY: return 82;
            case STRIDER: return 83;
            case TRADER_LLAMA: return 89;
            case TROPICAL_FISH: return 90;
            case TURTLE: return 91;
            case VEX: return 92;
            case VILLAGER: return 93;
            case VINDICATOR: return 94;
            case WANDERING_TRADER: return 95;
            case WITCH: return 96;
            case  WITHER: return  97;
            case WITHER_SKELETON: return 98;
            case WOLF: return 100;
            case ZOGLIN: return 101;
            case ZOMBIE: return 102;
            case ZOMBIE_HORSE: return 103;
            case ZOMBIE_VILLAGER: return 104;
            case ZOMBIFIED_PIGLIN: return 105;
            default: //villager
                return 93;
        }
    }
}
