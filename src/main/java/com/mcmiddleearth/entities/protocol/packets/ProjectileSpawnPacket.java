package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class ProjectileSpawnPacket extends AbstractPacket {

        private final PacketContainer spawn, velocity;

        private final McmeEntity entity;

    public ProjectileSpawnPacket(McmeEntity entity, Vector velo, McmeEntity shooter) {
            this.entity = entity;
            spawn = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
            spawn.getIntegers().write(0, entity.getEntityId())
                    .write(1, (int)(velo.getX()*8000)) //veo
                    .write(2, (int)(velo.getY()*8000))
                    .write(3, (int)(velo.getZ()*8000))
                    .write(6,shooter.getEntityId()+1); // object data
///Logger.getGlobal().info("Spawn eid: "+entity.getEntityId());
//Logger.getGlobal().info("id "+shooter.getEntityId()+" Velo: "+velo.getX()+" "+velo.getY()+" "+velo.getZ());
//Logger.getGlobal().info("Type "+entity.getType().getBukkitEntityType());
            spawn.getEntityTypeModifier().write(0,entity.getType().getBukkitEntityType());
            spawn.getUUIDs().write(0, entity.getUniqueId());

            velocity = new PacketContainer(PacketType.Play.Server.ENTITY_VELOCITY);
            velocity.getIntegers().write(0, entity.getEntityId())
                    .write(1, (int)(velo.getX()*8000)) //veo
                    .write(2, (int)(velo.getY()*8000))
                    .write(3, (int)(velo.getZ()*8000));
//Logger.getGlobal().info("uuid "+entity.getUniqueId());
            update();
        }

        @Override
        public void update() {
            Location loc = entity.getLocation();
            Vector velo = entity.getVelocity();
//Logger.getGlobal().info("update Velo: "+velo.getX()+" "+velo.getY()+" "+velo.getZ());
//Logger.getGlobal().info("Location "+loc);
            spawn.getIntegers()
                    .write(1, (int)(velo.getX()*8000)) //velo
                    .write(2, (int)(velo.getY()*8000))
                    .write(3, (int)(velo.getZ()*8000))
                    .write(4, (int)(loc.getYaw()*256/360)) //yaw
                    .write(5, (int)(loc.getPitch()*256/360)); //pitch
            spawn.getDoubles()
                    .write(0, loc.getX())
                    .write(1, loc.getY())
                    .write(2, loc.getZ());
            velocity.getIntegers()
                    .write(1, (int)(velo.getX()*8000)) //velo
                    .write(2, (int)(velo.getY()*8000))
                    .write(3, (int)(velo.getZ()*8000));
        }

        @Override
        public void send(Player recipient) {
//Logger.getGlobal().info("send non living spawn to : "+recipient.getName()+" with id: "+entity.getEntityId());
            send(spawn, recipient);
            send(velocity, recipient);
        }

    }
