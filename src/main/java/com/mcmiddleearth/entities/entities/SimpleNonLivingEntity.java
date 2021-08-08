package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.SimpleNonLivingEntitySpawnPacket;

public class SimpleNonLivingEntity extends SimpleEntity {

    public SimpleNonLivingEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        super(entityId, factory);
        spawnPacket = new SimpleNonLivingEntitySpawnPacket(this);
    }

}
