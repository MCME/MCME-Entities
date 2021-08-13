package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalVirtualEntityIsClose;
import org.bukkit.Location;

public class GoalLocationTargetGoto extends GoalLocationTarget {

    public GoalLocationTargetGoto(VirtualEntity entity, VirtualEntityGoalFactory factory, Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
    }

    @Override
    public void update() {
        if(isCloseToTarget(GoalDistance.POINT)) {
            EntitiesPlugin.getEntityServer().handleEvent(new GoalVirtualEntityIsClose(getEntity(),this));
            clearHeadGoals();
            setIsMoving(false);
            movementSpeed = MovementSpeed.STAND;
            setFinished();
        }
        super.update();
    }

}
