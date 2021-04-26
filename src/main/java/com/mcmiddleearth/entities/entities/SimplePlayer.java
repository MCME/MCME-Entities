package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.protocol.packets.SimplePlayerSpawnPacket;

public class SimplePlayer extends SimpleLivingEntity {

    public SimplePlayer(int entityId, VirtualEntityFactory factory) {
        super(entityId, factory);
        spawnPacket = new SimplePlayerSpawnPacket(this);
    }

}
