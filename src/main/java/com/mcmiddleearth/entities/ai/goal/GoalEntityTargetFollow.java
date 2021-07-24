package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.entity.Entity;

public class GoalEntityTargetFollow extends GoalEntityTarget {

    //private final int isCloseDistanceSquared = 4;

    public GoalEntityTargetFollow(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder, target);
    }

    @Override
    public void doTick() {
        super.doTick();
        if(isCloseToTarget(isCloseDistanceSquared*4)) {
//Logger.getGlobal().info("delete path as entity is close.");
            deletePath();
            setRotation(getEntity().getLocation().clone().setDirection(getTarget().getLocation().toVector()
                                                         .subtract(getEntity().getLocation().toVector())).getYaw());
        }
    }

    @Override
    public void update() {
        if(!(isCloseToTarget(isCloseDistanceSquared*4))) {
            super.update();
        }
    }

    @Override
    public boolean isFinished() {return false;}

}
