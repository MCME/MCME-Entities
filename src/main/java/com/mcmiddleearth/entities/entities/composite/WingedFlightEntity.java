package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.composite.bones.BoneThreeAxis;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class WingedFlightEntity extends BakedAnimationEntity {

    //private float pitch;
    private float currentRoll, currentPitch;

    private float maxRotationStepFlight = 2f;

    private final  Vector attackPoint;

    public WingedFlightEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        super(entityId, factory, RotationMode.YAW_PITCH_ROLL);
        maxRotationStepFlight = factory.getMaxRotationStepFlight();
        //pitch = factory.getgetPitch();
        currentRoll = factory.getRoll();
        instantAnimationSwitching = false;
        attackPoint = factory.getAttackPoint();
//Logger.getGlobal().info("RotationStepFlight: "+maxRotationStepFlight + " roll: "+currentRoll);
    }

    @Override
    protected void updateBodyBones() {
        if(getMovementType().equals(MovementType.FLYING) || getMovementType().equals(MovementType.GLIDING)) {
            currentPitch = turn(currentPitch, getLocation().getPitch(), maxRotationStepFlight);
            float yawDiff = getLocation().getYaw() - currentYaw;
            while (yawDiff < -180) yawDiff += 360;
            while (yawDiff > 180) yawDiff -= 360;
            float rollTarget;
            if(yawDiff>0) {
                rollTarget = Math.min(70, yawDiff * 1.6f);
            } else {
                rollTarget = Math.max(-70, yawDiff * 1.6f);
            }
//Logger.getGlobal().info("Yaw diff: "+yawDiff);
            currentRoll = turn(currentRoll, rollTarget, maxRotationStepFlight);
            currentYaw = turn(currentYaw, getLocation().getYaw(), Math.max(Math.abs(maxRotationStepFlight / 60 * currentRoll),maxRotationStepFlight/4f));
//Logger.getGlobal().info("Rotation: "+((int)currentYaw)+" "+((int)currentPitch)+" "+(int)currentRoll);
            getBones().stream().filter(bone -> !bone.isHeadBone()).forEach(bone -> {
                ((BoneThreeAxis) bone).setRotation(currentYaw, currentPitch, -currentRoll);
            });
        } else {
            getLocation().setPitch(0);
            currentPitch = 0;
            currentRoll = 0;
            super.updateBodyBones();
        }
    }

    @Override
    public float getRoll() {
        return currentRoll;
    }

    @Override
    public float getPitch() {
        return getLocation().getPitch();
    }

    @Override
    public void setRotation(float yaw, float pitch, float roll) {
        this.getLocation().setPitch(pitch);
        super.setRotation(yaw,pitch,roll);
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
                .withRoll(currentRoll)
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
