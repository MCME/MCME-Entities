package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.protocol.packets.SimpleNonLivingEntitySpawnPacket;

public class SimpleNonLivingEntity extends SimpleEntity {

    public SimpleNonLivingEntity(int entityId, VirtualEntityFactory factory) {
        super(entityId, factory);
        spawnPacket = new SimpleNonLivingEntitySpawnPacket(this);
    }

}
