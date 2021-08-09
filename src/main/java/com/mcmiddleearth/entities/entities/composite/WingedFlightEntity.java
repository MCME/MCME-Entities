package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.composite.bones.BoneThreeAxis;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;

public class WingedFlightEntity extends BakedAnimationEntity {

    private float pitch;
    private float currentYaw, currentRoll, currentPitch;

    public WingedFlightEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        super(entityId, factory);
        maxRotationStep = 10f;
    }

    @Override
    protected void updateBodyBones() {
        currentPitch = turn(currentPitch, pitch, maxRotationStep);
        float yawDiff = getLocation().getYaw() - currentYaw;
        while(yawDiff < -180) yawDiff += 360;
        while(yawDiff > 180)  yawDiff -= 360;
        float rollTarget = Math.min(60, yawDiff * 0.66f);
        currentRoll = turn(currentRoll,rollTarget, maxRotationStep);
        currentYaw = turn(currentYaw, getLocation().getYaw(),maxRotationStep / 60 * currentRoll);
        getBones().stream().filter(bone->!bone.isHeadBone()).forEach(bone-> {
            ((BoneThreeAxis)bone).setRotation(currentYaw,currentPitch,currentRoll);
        });
    }

    public float getRoll() {
        return currentRoll;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean hasRotationUpdate() {
        return currentPitch!=pitch || currentYaw != getLocation().getYaw();
    }
}
