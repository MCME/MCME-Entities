package com.mcmiddleearth.entities.entities.composite;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.composite.animation.Animation;
import com.mcmiddleearth.entities.protocol.packets.CompositeEntityMovePacket;
import com.mcmiddleearth.entities.protocol.packets.CompositeEntitySpawnPacket;
import com.mcmiddleearth.entities.protocol.packets.CompositeEntityTeleportPacket;
import com.mcmiddleearth.entities.protocol.packets.VirtualEntityDestroyPacket;
import org.bukkit.Material;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class CompositeEntity extends VirtualEntity {

    private final Set<Bone> bones = new HashSet<>();

    private final int firstEntityId;

    public CompositeEntity(int entityId, VirtualEntityFactory factory) {
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
