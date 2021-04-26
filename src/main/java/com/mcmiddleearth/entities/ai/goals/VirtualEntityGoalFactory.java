package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.movement.MovementType;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.ai.pathfinding.WalkingPathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class VirtualEntityGoalFactory {

    private Location targetLocation;

    private McmeEntity targetEntity;

    private GoalType goalType;

    public VirtualEntityGoalFactory withTargetLocation(Location target) {
        this.targetLocation = target;
        return this;
    }

    public VirtualEntityGoalFactory withTargetEntity(McmeEntity target) {
        this.targetEntity = target;
        return this;
    }

    public VirtualEntityGoalFactory withGoalType(GoalType goalType) {
        this.goalType = goalType;
        return this;
    }

    public VirtualEntityGoal build(VirtualEntity entity) {
        MovementType movementType = entity.getMovementType();
        Pathfinder pathfinder;
        VirtualEntityGoal goal;
        switch(movementType) {
            case WALKING: pathfinder = new WalkingPathfinder(entity);
                break;
            default:
                pathfinder = new WalkingPathfinder(entity);
        }
        switch(goalType) {
            case FOLLOW_ENTITY:
                goal = new FollowEntityGoal(goalType,entity,pathfinder,targetEntity);
                break;
            default:
                goal = null;
        }
        return goal;
    }
}
