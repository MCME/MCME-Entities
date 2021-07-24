package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.VirtualEntityFactory;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.CompositeEntityMovePacket;
import com.mcmiddleearth.entities.protocol.packets.CompositeEntitySpawnPacket;
import com.mcmiddleearth.entities.protocol.packets.CompositeEntityTeleportPacket;
import com.mcmiddleearth.entities.protocol.packets.VirtualEntityDestroyPacket;

import java.util.HashSet;
import java.util.Set;

public abstract class CompositeEntity extends VirtualEntity {

    private final Set<Bone> bones = new HashSet<>();

    private final int firstEntityId;

    public CompositeEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException {
        super(factory);
        firstEntityId = entityId;
    }

    protected void createPackets() {
        spawnPacket = new CompositeEntitySpawnPacket(this);
        int[] ids = new int[bones.size()];
        for(int i = 0; i < bones.size(); i++) {
            ids[i] = firstEntityId+i;
        }
        removePacket = new VirtualEntityDestroyPacket(ids);
        teleportPacket = new CompositeEntityTeleportPacket(this);
        movePacket = new CompositeEntityMovePacket(this);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void move() {
        bones.forEach(Bone::move);
        super.move();
        bones.forEach(Bone::resetUpdateFlags);
    }

    public void setRotation(float yaw) {
        super.setRotation(yaw);
        //bones.forEach(bone->bone.setRotation(yaw));
    }

    public Set<Bone> getBones() {
        return bones;
    }

    @Override
    public int getEntityId() {
        return firstEntityId;
    }

    @Override
    public int getEntityQuantity() {
        return bones.size();
    }

}
