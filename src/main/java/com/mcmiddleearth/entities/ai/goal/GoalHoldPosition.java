package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.goal.head.HeadGoalEntityTarget;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalStare;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWaypointTarget;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;


public class GoalHoldPosition extends GoalVirtualEntity {

    private final float yaw, pitch;

    private boolean hasRotation = true, tick = false;

    public GoalHoldPosition(GoalType type, VirtualEntity entity, Location target) {
        super(type, entity);
        Location orientation = getEntity().getLocation().clone()
                .setDirection(target.toVector().subtract(getEntity().getLocation().toVector()));
        yaw = orientation.getYaw();
        pitch = orientation.getPitch();
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

    @Override
    public boolean isFinished() {
        return false;
    }

    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalStare(yaw,pitch));
    }

}
