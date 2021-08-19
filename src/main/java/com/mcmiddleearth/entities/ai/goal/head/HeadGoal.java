package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;

public abstract class HeadGoal {

    private int duration = 10;
    protected float yaw, pitch;

    public float getHeadYaw() {
        return yaw;
    }

    public float getHeadPitch() {
        return pitch;
    }

    public boolean hasHeadRotation() {return true; }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public abstract boolean provideGoalAndEntity(Goal goal, McmeEntity entity);

    public abstract void doTick();

    public void resetRotationFlags() {}

}
