package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.movement.MovementType;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

import java.util.logging.Logger;

public class FollowEntityGoal extends EntityTargetingGoal {

    private final int isCloseDistanceSquared = 4;

    public FollowEntityGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder, target);
    }

    @Override
    public void doTick() {
        super.doTick();
        if(isCloseToTarget(isCloseDistanceSquared)) {
//Logger.getGlobal().info("delete path as entity is close.");
            deletePath();
            setRotation(getEntity().getLocation().clone().setDirection(getTarget().getLocation().toVector()
                                                         .subtract(getEntity().getLocation().toVector())).getYaw());
        }
    }

    @Override
    public void update() {
        if(!isCloseToTarget(isCloseDistanceSquared)) {
            super.update();
        }
    }

    @Override
    public boolean isFinished() {return false;}

}
