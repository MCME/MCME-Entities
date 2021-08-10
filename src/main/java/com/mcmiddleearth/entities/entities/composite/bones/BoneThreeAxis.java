package com.mcmiddleearth.entities.entities.composite.bones;

import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import com.mcmiddleearth.entities.util.RotationMatrix;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class BoneThreeAxis extends BoneTwoAxis {

    private float roll;

    public BoneThreeAxis(String name, CompositeEntity parent, EulerAngle headPose, Vector relativePosition,
                         ItemStack headItem, boolean isHeadBone, int headPoseDelay) {
        super(name, parent, headPose, relativePosition, headItem, isHeadBone, headPoseDelay);
    }

    public void move() {
        if(hasHeadPoseUpdate) {
            rotatedHeadPose = RotationMatrix.rotateXZEulerAngleDegree(headPose,pitch,roll);
        }
        Vector shift;
        if(hasRotationUpdate()) {
            Vector rotatedZ = RotationMatrix.fastRotateZ(relativePosition,-roll);
            Vector rotatedZX = RotationMatrix.fastRotateX(rotatedZ,pitch);
            Vector newRelativePositionRotated = RotationMatrix.fastRotateY(rotatedZX,-yaw);
            shift = newRelativePositionRotated.clone().subtract(this.relativePositionRotated);
/*if(getName().equalsIgnoreCase("bone4")) {
    Logger.getGlobal().info("Rotate 3 axis");
    Logger.getGlobal().info("orig: " + relativePosition);
    Logger.getGlobal().info("roll: " + rotatedZ);
    Logger.getGlobal().info("pitc: " + rotatedZX);
    Logger.getGlobal().info("newr: " + newRelativePositionRotated);
}*/
            relativePositionRotated = newRelativePositionRotated;
        } else {
            shift = new Vector(0,0,0);
        }


        velocity = parent.getVelocity().clone().add(shift);
/*if(getName().equalsIgnoreCase("bone4")) {
    Logger.getGlobal().info("velo: "+velocity);
}*/

    }

    public void teleport() {
        if(hasHeadPoseUpdate) {
            rotatedHeadPose = RotationMatrix.rotateXZEulerAngleDegree(headPose, pitch, roll);
        }
        Vector rotatedZ = RotationMatrix.fastRotateZ(relativePosition,-roll);
        Vector rotatedZX = RotationMatrix.fastRotateX(rotatedZ,pitch);
        Vector newRelativePositionRotated = RotationMatrix.fastRotateY(rotatedZX,-yaw);
/*if(getName().equalsIgnoreCase("bone4")) {
    Logger.getGlobal().info("Rotate 3 axis");
    Logger.getGlobal().info("orig: " + relativePosition);
    Logger.getGlobal().info("roll: " + rotatedZ);
    Logger.getGlobal().info("pitc: " + rotatedZX);
    Logger.getGlobal().info("newr: " + newRelativePositionRotated);
}*/
        relativePositionRotated = newRelativePositionRotated;
        /*relativePositionRotated = RotationMatrix.fastRotateY(RotationMatrix
                .fastRotateX(RotationMatrix.fastRotateZ(relativePosition,roll),pitch),-yaw);*/
    }

    public void setRotation(float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        rotationUpdate = true;
        hasHeadPoseUpdate = true;
    }
}
