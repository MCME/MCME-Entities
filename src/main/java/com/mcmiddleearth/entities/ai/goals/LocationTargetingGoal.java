package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public abstract class LocationTargetingGoal extends PathGoal {

    public Location target;

    @Override
    public boolean hasHeadRotation() {
        return false;
    }

    @Override
    public float getHeadYaw() {
        return 0;
    }

    @Override
    public float getHeadPitch() {
        return 0;
    }

    @Override
    public boolean hasRotation() {
        return true;
    }

    @Override
    public float getRotation() {
        return getEntity().getLocation().clone()
                .setDirection(target.toVector().subtract(getEntity().getLocation().toVector()))
                .getYaw();
    }

    public LocationTargetingGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder, Location target) {
        super(type, entity, pathfinder);
        this.target = target;
    }

    public Location getTarget() {
        return target;
    }

    public void setTarget(Location target) {
        this.target = target;
    }

    public boolean isCloseToTarget(int distanceSquared) {
        return getEntity().getLocation().toVector().distanceSquared(getTarget().toVector()) < distanceSquared;
    }

}
