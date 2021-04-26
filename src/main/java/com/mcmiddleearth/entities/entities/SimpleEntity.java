package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.protocol.packets.SimpleEntityMovePacket;
import com.mcmiddleearth.entities.protocol.packets.VirtualEntityDestroyPacket;
import com.mcmiddleearth.entities.protocol.packets.SimpleEntityTeleportPacket;

public abstract class SimpleEntity extends VirtualEntity {

    int entityId;

    public SimpleEntity(int entityId, VirtualEntityFactory factory) {
        super(factory);
        this.entityId = entityId;
        teleportPacket = new SimpleEntityTeleportPacket(this);
        movePacket = new SimpleEntityMovePacket(this);
        removePacket = new VirtualEntityDestroyPacket(entityId);
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
