package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.*;

public abstract class SimpleEntity extends VirtualEntity {

    int entityId;

    public SimpleEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException {
        super(factory);
        this.entityId = entityId;
        teleportPacket = new SimpleEntityTeleportPacket(this);
        movePacket = new SimpleEntityMovePacket(this);
        removePacket = new VirtualEntityDestroyPacket(entityId);
        statusPacket = new SimpleEntityStatusPacket(entityId);
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public int getEntityQuantity() {
        return 1;
    }

}
