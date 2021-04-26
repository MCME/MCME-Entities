package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.protocol.packets.SimpleLivingEntitySpawnPacket;

public class SimpleLivingEntity extends SimpleEntity {

    public SimpleLivingEntity(int entityId, VirtualEntityFactory factory) {
        super(entityId, factory);
        spawnPacket = new SimpleLivingEntitySpawnPacket(this);
    }

}
