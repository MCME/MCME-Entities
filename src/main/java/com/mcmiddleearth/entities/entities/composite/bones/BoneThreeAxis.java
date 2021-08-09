package com.mcmiddleearth.entities.entities.composite.bones;

import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import com.mcmiddleearth.entities.util.RotationMatrix;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class BoneThreeAxis extends Bone {

    public BoneThreeAxis(String name, CompositeEntity parent, EulerAngle headPose, Vector relativePosition,
                         ItemStack headItem, boolean isHeadBone, int headPoseDelay) {
        super(name, parent, headPose, relativePosition, headItem, isHeadBone, headPoseDelay);
    }

    public void move() {
        if(hasHeadPoseUpdate) {
            rotatedHeadPose = RotationMatrix.rotateXZEulerAngleDegree(headPose,pitch);
        }
        Vector shift;
        if(hasRotationUpdate()) {
            Vector newRelativePositionRotated = RotationMatrix.fastRotateY(RotationMatrix
                    .fastRotateX(relativePosition.clone().subtract(parent.getHeadPitchCenter()),pitch).add(parent.getHeadPitchCenter()),-yaw);
            shift = newRelativePositionRotated.clone().subtract(this.relativePositionRotated);
            relativePositionRotated = newRelativePositionRotated;
        } else {
            shift = new Vector(0,0,0);
        }


        velocity = parent.getVelocity().clone().add(shift);

    }

    public void teleport() {
        if(hasHeadPoseUpdate) {
            rotatedHeadPose = RotationMatrix.rotateXZEulerAngleDegree(headPose, pitch);
        }
        relativePositionRotated = RotationMatrix.fastRotateY(RotationMatrix
                .fastRotateX(relativePosition.clone().subtract(parent.getHeadPitchCenter()),pitch).add(parent.getHeadPitchCenter()),-yaw);
    }

    public void setRotation(float yaw, float pitch, float roll) {

    }
}
