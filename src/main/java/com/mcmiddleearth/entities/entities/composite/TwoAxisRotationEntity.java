package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.composite.bones.BoneThreeAxis;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class TwoAxisRotationEntity extends BakedAnimationEntity {

    //private float pitch;
    private float currentPitch;

    private float maxRotationStepFlight = 2f;

    private final  Vector attackPoint;

    public TwoAxisRotationEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        super(entityId, factory, RotationMode.YAW_PITCH_ROLL);
        maxRotationStepFlight = factory.getMaxRotationStepFlight();
        //pitch = factory.getgetPitch();
        currentPitch = getPitch();
        instantAnimationSwitching = false;
        attackPoint = factory.getAttackPoint();
//Logger.getGlobal().info("RotationStepFlight: "+maxRotationStepFlight + " roll: "+currentRoll);
    }

    @Override
    protected void updateBodyBones() {
        if(getMovementType().equals(MovementType.SWIMMING)) {
            currentPitch = turn(currentPitch, getLocation().getPitch(), maxRotationStepFlight);
            float yawDiff = getLocation().getYaw() - currentYaw;
            while (yawDiff < -180) yawDiff += 360;
            while (yawDiff > 180) yawDiff -= 360;
//Logger.getGlobal().info("Yaw diff: "+yawDiff);
            currentYaw = turn(currentYaw, getLocation().getYaw(), maxRotationStepFlight);
//Logger.getGlobal().info("Rotation: "+((int)currentYaw)+" "+((int)currentPitch)+" "+(int)currentRoll);
            getBones().stream().filter(bone -> !bone.isHeadBone()).forEach(bone -> {
                ((BoneThreeAxis) bone).setRotation(currentYaw, currentPitch, 0);
            });
        } else {
            getLocation().setPitch(0);
            currentPitch = 0;
            super.updateBodyBones();
        }
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public float getPitch() {
        return getLocation().getPitch();
    }

    @Override
    public void setRotation(float yaw, float pitch, float roll) {
        this.getLocation().setPitch(pitch);
        super.setRotation(yaw,pitch,0);
    }
    /*public void setPitch(float pitch) {
        this.pitch = pitch;
    }*/

    @Override
    public boolean hasRotationUpdate() {
        return currentPitch!=getPitch() || currentYaw != getLocation().getYaw();
    }

    /*@Override
    public RotationMode getRotationMode() {
        return RotationMode.YAW_PITCH_ROLL;
    }*/

    public float getCurrentPitch() {
        return currentPitch;
    }

    public float getCurrentYaw() {
        return currentYaw;
    }

    public float getMaxRotationStepFlight() {
        return maxRotationStepFlight;
    }

    public void setMaxRotationStepFlight(float maxRotationStepFlight) {
        this.maxRotationStepFlight = maxRotationStepFlight;
    }

    public Vector getAttackPoint() {
        return attackPoint;
    }

    @Override
    public VirtualEntityFactory getFactory() {
        VirtualEntityFactory factory = super.getFactory()
                .withMaxRotationStepFlight(maxRotationStepFlight)
                .withAttackPoint(attackPoint);
        return factory;
    }

    @Override
    public MovementSpeed getMovementSpeedAnimation() {
        float angle = getVelocity().angle(new Vector(getVelocity().getX(),0,getVelocity().getZ()));
//Logger.getGlobal().info("angle: " + angle);
        if(angle < 0.174) { //10 * PI / 180
            return MovementSpeed.WALK;
        } else if(getVelocity().getY() < 0) {
            return MovementSpeed.SPRINT;
        } else {
            return MovementSpeed.SLOW;
        }
    }
}
