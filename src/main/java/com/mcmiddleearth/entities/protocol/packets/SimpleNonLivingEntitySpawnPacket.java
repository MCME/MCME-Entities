package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.SimpleNonLivingEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Logger;

public class SimpleNonLivingEntitySpawnPacket extends AbstractPacket {

    private final PacketContainer spawn;

    private final McmeEntity entity;

    public SimpleNonLivingEntitySpawnPacket(McmeEntity entity) {
        this.entity = entity;
        spawn = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawn.getIntegers().write(0, entity.getEntityId())
                        .write(1, 0) //velocity
                        .write(2, 0)
                        .write(3, 0)
                        .write(6,0); // object data
//Logger.getGlobal().info("id "+entity.getEntityId());
//Logger.getGlobal().info("Type "+entity.getType().getBukkitEntityType());
        spawn.getEntityTypeModifier().write(0,entity.getType().getBukkitEntityType());
        spawn.getUUIDs().write(0, entity.getUniqueId());
//Logger.getGlobal().info("uuid "+entity.getUniqueId());
        update();
    }

    @Override
    public void update() {
        Location loc = entity.getLocation();
//Logger.getGlobal().info("Location "+loc);
        spawn.getIntegers()
                .write(4, (int)(loc.getYaw()*256/360)) //yaw
                .write(5, (int)(loc.getPitch()*256/360)); //pitch
        spawn.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
    }

    @Override
    public void send(Player recipient) {
//Logger.getGlobal().info("send non living spawn to : "+recipient.getName()+" with id: "+entity.getEntityId());
        send(spawn, recipient);
    }

}
