package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.*;
import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public abstract class CompositeEntity extends VirtualEntity {

    private final Set<Bone> bones = new HashSet<>();
    //private final Set<Bone> headBones = new HashSet<>();

    private final int firstEntityId;

    private Bone displayNameBone;

    private Vector headPitchCenter;

    private int headPoseDelay;

    //private boolean rotationUpdate;
    private float currentYaw, currentPitch, currentHeadYaw;
    private static final float maxRotationStep = 10f;

    public CompositeEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException {
        super(factory);
        firstEntityId = entityId;
        headPitchCenter = factory.getHeadPitchCenter();
        headPoseDelay = factory.getHeadPoseDelay();
        if(getDisplayName()!=null) {
            displayNameBone = new Bone("displayName",this,new EulerAngle(0,0,0),
                                       factory.getDisplayNamePosition(),null,false, 0);
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
        checkHeadYaw();
        if(hasRotationUpdate()) {
            updateBodyBones();
        }
        if(hasLookUpdate()) {
            updateHeadBones();
        }
//Logger.getGlobal().info("Rotation: "+hasRotationUpdate()+" "+getLocation().getYaw() +" "+currentYaw);
//Logger.getGlobal().info("Head Yaw: "+currentHeadYaw+" "+currentPitch+" "+getVelocity().toString());
        bones.forEach(Bone::move);
        super.move();
        bones.forEach(Bone::resetUpdateFlags);
    }

    @Override
    public void teleport() {
//Logger.getGlobal().info("Composite teleport method");
        checkHeadYaw();
        updateBodyBones();
        updateHeadBones();
        bones.forEach(Bone::teleport);
        super.teleport();
        bones.forEach(Bone::resetUpdateFlags);
    }

    private void updateBodyBones() {
        currentYaw = turn(currentYaw,getLocation().getYaw());
        bones.stream().filter(bone->!bone.isHeadBone()).forEach(bone-> {
            bone.setRotation(currentYaw);
        });
    }

    private void updateHeadBones() {
        currentHeadYaw = turn(currentHeadYaw, getHeadYaw());
        currentPitch = turn(currentPitch,getLocation().getPitch());
        bones.stream().filter(Bone::isHeadBone).forEach(bone-> {
            bone.setRotation(currentHeadYaw);
            bone.setPitch(currentPitch);
        });
    }

    private float turn(float currentAngle, float aimAngle) {
        float diff = aimAngle - currentAngle;
        while(diff < -180) {
            diff += 360;
        }
        while(diff > 180) {
            diff -= 360;
        }
        if(Math.abs(diff)<maxRotationStep) {
            return aimAngle;
        } else if(diff<0) {
            return currentAngle - maxRotationStep;
        } else {
            return currentAngle + maxRotationStep;
        }
    }

    private void checkHeadYaw() {
        float diff = getHeadYaw() - getLocation().getYaw();
        while(diff < -180) {
            diff += 360;
        }
        while(diff > 180) {
            diff -= 360;
        }
        if(diff > 90) {
            setHeadRotation(getLocation().getYaw()+90f,getLocation().getPitch());
        }
        else if(diff < -90) {
            setHeadRotation(getLocation().getYaw() - 90f, getLocation().getPitch());
        }
    }

    @Override
    public boolean hasLookUpdate() {
        return currentPitch != getLocation().getPitch() || currentHeadYaw != getHeadYaw();
    }

    @Override
    public boolean hasRotationUpdate() {
        return currentYaw != getLocation().getYaw();
    }

    public Vector getHeadPitchCenter() {
        return headPitchCenter;
    }

    public void setHeadPitchCenter(Vector headPitchCenter) {
        this.headPitchCenter = headPitchCenter;
    }

    /*public void setRotation(float yaw) {
        bones.stream().filter(bone->!bone.isHeadBone()).forEach(bone-> {
            bone.setRotation(yaw);
        });
        super.setRotation(yaw);
        //bones.forEach(bone->bone.setRotation(yaw));
    }

    @Override
    public void setHeadRotation(float yaw, float pitch) {
        bones.stream().filter(Bone::isHeadBone).forEach(bone-> {
            bone.setRotation(yaw);
            bone.setPitch(pitch);
        });
    }*/


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

    public int getHeadPoseDelay() {
        return headPoseDelay;
    }

    @Override
    public boolean hasId(int entityId) {
        return this.firstEntityId <= entityId && this.firstEntityId+getEntityQuantity() > entityId;
    }
}
