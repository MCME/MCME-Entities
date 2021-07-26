package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class GoalLocationTargetGoto extends GoalLocationTarget {

    public GoalLocationTargetGoto(GoalType type, VirtualEntity entity, Pathfinder pathfinder, Location target) {
        super(type, entity, pathfinder, target);
    }

    @Override
    public void update() {
        if(isCloseToTarget(GoalDistance.POINT)) {
            clearHeadGoals();
            setIsMoving(false);
        }
        super.update();
    }

    @Override
    public boolean isFinished() {
        return isCloseToTarget(GoalDistance.POINT);
    }

}
