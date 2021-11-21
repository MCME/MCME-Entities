package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.composite.bones.Bone;
import com.mcmiddleearth.entities.entities.composite.bones.BoneThreeAxis;
import com.mcmiddleearth.entities.entities.composite.bones.BoneTwoAxis;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.*;
import com.mcmiddleearth.entities.protocol.packets.composite.CompositeEntityMovePacket;
import com.mcmiddleearth.entities.protocol.packets.composite.CompositeEntitySpawnPacket;
import com.mcmiddleearth.entities.protocol.packets.composite.CompositeEntityTeleportPacket;
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
    private Vector displayNamePosition;

    private Vector headPitchCenter;

    private int headPoseDelay;

    //private ActionType animation = null;

    //private boolean rotationUpdate;
    protected float currentYaw, currentHeadPitch, currentHeadYaw;

    protected float maxRotationStep = 40f;

    protected RotationMode rotationMode;

    public CompositeEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        this(entityId,factory, RotationMode.YAW);
    }

    protected CompositeEntity(int entityId, VirtualEntityFactory factory,
                              RotationMode rotationMode) throws InvalidLocationException, InvalidDataException {
        super(factory);
        firstEntityId = entityId;
        headPitchCenter = factory.getHeadPitchCenter().clone();
        headPoseDelay = factory.getHeadPoseDelay();
        this.rotationMode = rotationMode;
        maxRotationStep = factory.getMaxRotationStep();
        currentYaw = getYaw();
        currentHeadYaw = getHeadYaw();
        currentHeadPitch = getHeadPitch();
        displayNamePosition = factory.getDisplayNamePosition().clone();
        if(getDisplayName()!=null) {
            createDisplayBone();
        }
    }

    protected CompositeEntity(int entityId, McmeEntityType type, Location location) {
        super(type, location);
        firstEntityId = entityId;
        headPitchCenter = new Vector(0,0,0);
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
        //namePacket = new DisplayNamePacket(firstEntityId);
    }

    private void createDisplayBone() {
        if (rotationMode.equals(RotationMode.YAW_PITCH_ROLL)) {
            displayNameBone = new BoneThreeAxis("displayName", this, new EulerAngle(0, 0, 0),
                    displayNamePosition, null, false, 0);
        } else {
            displayNameBone = new Bone("displayName", this, new EulerAngle(0, 0, 0),
                    displayNamePosition, null, false, 0);
        }
        displayNameBone.setDisplayName(getDisplayName());
        bones.add(displayNameBone);
    }

    @Override
    public String getName() {
        return super.getName();
    }

/*    @Override
    public void doTick() {
        if(animation!=null) {
            if (animation.equals(ActionType.HURT)) {
                bones.forEach(bone -> {
                    bone.getAnimationPacket().setAnimation(SimpleEntityAnimationPacket.AnimationType.TAKE_DAMAGE);
                    getViewers().forEach(viewer -> {
                        bone.getAnimationPacket().send(viewer);
Logger.getGlobal().info("Sending animation: "+viewer.getName());
                    });
                });
            }
            animation = null;
        }
        super.doTick();
    }*/

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
        bones.forEach(bone -> bone.move());
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

    protected void updateBodyBones() {
        currentYaw = turn(currentYaw,getLocation().getYaw(),maxRotationStep);
        bones.stream().filter(bone->!bone.isHeadBone()).forEach(bone-> {
            bone.setRotation(currentYaw);
        });
    }

    private void updateHeadBones() {
        currentHeadYaw = turn(currentHeadYaw, getHeadYaw(),maxRotationStep);
        currentHeadPitch = turn(currentHeadPitch,getHeadPitch(),maxRotationStep);
        bones.stream().filter(bone -> (bone instanceof  BoneTwoAxis) && bone.isHeadBone()).forEach(bone-> {
            bone.setRotation(currentHeadYaw);
            ((BoneTwoAxis)bone).setPitch(currentHeadPitch);
        });
    }

    protected float turn(float currentAngle, float aimAngle, float maxStep) {
        float diff = aimAngle - currentAngle;
        while(diff < -180) {
            diff += 360;
        }
        while(diff > 180) {
            diff -= 360;
        }
        if(Math.abs(diff)<maxStep) {
            return aimAngle;
        } else if(diff<0) {
            return currentAngle - maxStep;
        } else {
            return currentAngle + maxStep;
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
        float maxYaw = Math.min(90, 180 - 2 * Math.abs(getLocation().getPitch()));
        if(diff > maxYaw) {//90) {
            setHeadRotation(getLocation().getYaw()+maxYaw/*90f*/,getLocation().getPitch());
        }
        else if(diff < -90) {
            setHeadRotation(getLocation().getYaw() - maxYaw/*90f*/, getLocation().getPitch());
        }
    }

    @Override
    public boolean hasLookUpdate() {
        return currentHeadPitch != getLocation().getPitch() || currentHeadYaw != getHeadYaw();
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
        if (displayNameBone != null) {
            super.setDisplayName(displayName);
            displayNameBone.setDisplayName(displayName);
        }
    }

    public int getHeadPoseDelay() {
        return headPoseDelay;
    }

    @Override
    public boolean hasId(int entityId) {
        return this.firstEntityId <= entityId && this.firstEntityId+getEntityQuantity() > entityId;
    }

    public RotationMode getRotationMode() {
        return rotationMode;
    }

    /*@Override
    public void playAnimation(ActionType type) {
        this.animation = type;
    }*/

    public float getMaxRotationStep() {
        return maxRotationStep;
    }

    public void setMaxRotationStep(float maxRotationStep) {
        this.maxRotationStep = maxRotationStep;
    }

    public float getCurrentYaw() {
        return currentYaw;
    }

    public enum RotationMode {
        YAW, YAW_PITCH, YAW_PITCH_ROLL;
    }

    public VirtualEntityFactory getFactory() {
        VirtualEntityFactory factory = super.getFactory()
            .withHeadPoseDelay(headPoseDelay)
            .withHeadPitchCenter(headPitchCenter)
            .withMaxRotationStep(maxRotationStep);
        if(getDisplayName() != null && displayNameBone != null) {
            factory.withDisplayNamePosition(displayNameBone.getRelativePosition());
        }
        return factory;
    }

}
