package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.entities.McmeEntityType;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.VirtualEntityFactory;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.*;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;

import java.util.HashSet;
import java.util.Set;

public abstract class CompositeEntity extends VirtualEntity {

    private final Set<Bone> bones = new HashSet<>();

    private final int firstEntityId;

    private Bone displayNameBone;

    public CompositeEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException {
        super(factory);
        firstEntityId = entityId;
        if(getDisplayName()!=null) {
            displayNameBone = new Bone("displayName",this,new EulerAngle(0,0,0),factory.getDisplayNamePosition(),null);
            displayNameBone.setDisplayName(factory.getDisplayName());
            bones.add(displayNameBone);
        }
    }

    protected CompositeEntity(int entityId, McmeEntityType type, Location location) {
        super(type, location);
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
        namePacket = new DisplayNamePacket(firstEntityId);
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

    @Override
    public void teleport() {
        bones.forEach(Bone::teleport);
        super.teleport();
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

    @Override
    public void setDisplayName(String displayName) {
        if(displayNameBone!=null) {
            super.setDisplayName(displayName);
        }
    }

}
