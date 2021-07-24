package com.mcmiddleearth.entities.ai.goal.head;

public abstract class HeadGoal {

    private int duration = 10;
    protected float yaw, pitch;

    public float getHeadYaw() {
        return yaw;
    }

    public float getHeadPitch() {
        return pitch;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public abstract void doTick();

}
