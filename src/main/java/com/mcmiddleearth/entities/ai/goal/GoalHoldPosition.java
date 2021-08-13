package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.goal.head.HeadGoalEntityTarget;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalStare;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWaypointTarget;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;


public class GoalHoldPosition extends GoalVirtualEntity {

    private final float yaw, pitch;

    private final Location targetLocation;

    private boolean hasRotation = true, tick = false;

    public GoalHoldPosition(VirtualEntity entity, VirtualEntityGoalFactory factory) {
        super(entity, factory);
        Location orientation = getEntity().getLocation().clone()
                .setDirection(factory.getTargetLocation().toVector().subtract(getEntity().getLocation().toVector()));
        yaw = orientation.getYaw();
        pitch = orientation.getPitch();
        this.targetLocation = factory.getTargetLocation();
        setDefaultHeadGoal();
    }

    @Override
    public void doTick() {
        super.doTick();
        if(tick) hasRotation = false;
        if(!tick) tick = true;
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
        return yaw;
    }

    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalStare(yaw,pitch));
    }

    @Override
    public float getYaw() {
        return yaw;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public VirtualEntityGoalFactory getFactory() {
        return super.getFactory().withTargetLocation(targetLocation);
    }
}
