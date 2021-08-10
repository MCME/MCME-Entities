package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWatch;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class GoalWatchEntity extends GoalVirtualEntity {

    private final McmeEntity target;

    private boolean hasRotation;
    private float rotation;

    private boolean secondUpdate = false;

    public GoalWatchEntity(GoalType type, VirtualEntity entity, McmeEntity target) {
        super(type, entity);
        this.target = target;
        setDefaultHeadGoal();
    }

    @Override
    public void update() {
        super.update();
        if(secondUpdate) {
            Location orientation = getEntity().getLocation().clone()
                    .setDirection(target.getLocation().toVector().subtract(getEntity().getLocation().toVector()));
            rotation = orientation.getYaw();
            hasRotation = true;
        }
        secondUpdate = !secondUpdate;
    }

    @Override
    public void doTick() {
        super.doTick();
        hasRotation = false;
    }

    @Override
    public Vector getDirection() {
        return null;
    }

    @Override
    public boolean hasRotation() {
        return hasRotation;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalWatch(target,getEntity()));
    }

    @Override
    public float getYaw() {
        return getEntity().getLocation().clone()
                .setDirection(target.getLocation().toVector().subtract(getEntity().getLocation().toVector()))
                .getYaw();
    }

    @Override
    public float getPitch() {
        return getEntity().getLocation().clone()
                .setDirection(target.getLocation().toVector().subtract(getEntity().getLocation().toVector()))
                .getPitch();
    }

    @Override
    public float getRoll() {
        return 0;
    }
}
