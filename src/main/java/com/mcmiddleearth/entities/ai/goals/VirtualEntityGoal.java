package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.util.Vector;

import java.util.Random;

public abstract class VirtualEntityGoal implements Goal{

    private GoalType type;

    private final VirtualEntity entity;

    private int updateInterval = 10;

    private final int updateRandom;

    public VirtualEntityGoal(GoalType type, VirtualEntity entity) {
        this.type = type;
        this.entity = entity;
        updateRandom = new Random().nextInt(updateInterval);
    }

    public abstract Vector getDirection();

    public abstract boolean hasHeadRotation();

    public abstract float getHeadYaw();

    public abstract float getHeadPitch();

    public abstract boolean hasRotation();

    public abstract float getRotation();

    public abstract void update();

    public abstract void doTick();

    public VirtualEntity getEntity() {
        return entity;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getUpdateRandom() {
        return updateRandom;
    }
}
