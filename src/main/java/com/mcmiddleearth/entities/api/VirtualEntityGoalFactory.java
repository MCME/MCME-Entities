package com.mcmiddleearth.entities.api;

import com.mcmiddleearth.entities.ai.goal.*;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.ai.pathfinding.SimplePathfinder;
import com.mcmiddleearth.entities.ai.pathfinding.WalkingPathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.util.Constrain;
import org.bukkit.Location;

import java.util.logging.Logger;

public class VirtualEntityGoalFactory {

    private Location targetLocation;
    private Location[] checkpoints;
    private boolean loop;

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

    public VirtualEntityGoalFactory withCheckpoints(Location[] checkpoints) {
        this.checkpoints = checkpoints;
        return this;
    }

    public VirtualEntityGoalFactory withLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public GoalVirtualEntity build(VirtualEntity entity) throws InvalidLocationException, InvalidDataException {
        if(goalType == null) {
            return null;
        }
        MovementType movementType = entity.getMovementType();
//Logger.getGlobal().info("movement type: "+movementType);
//Logger.getGlobal().info("target: "+targetEntity);
        Pathfinder pathfinder;
        GoalVirtualEntity goal;
        switch(movementType) {
            case UPRIGHT:
            case SNEAKING:
                pathfinder = new WalkingPathfinder(entity);
//Logger.getGlobal().info("walking pathfinding!");
                break;
            default:
//Logger.getGlobal().info("Simple pathfinding!");
                pathfinder = new SimplePathfinder();
        }
        switch(goalType) {
            case WATCH_ENTITY:
                Constrain.checkEntity(targetEntity);
                Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalWatchEntity(goalType,entity,targetEntity);
                break;
            case FOLLOW_ENTITY:
                Constrain.checkEntity(targetEntity);
                Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalEntityTargetFollow(goalType,entity,pathfinder,targetEntity);
                break;
            case ATTACK_ENTITY:
                Constrain.checkEntity(targetEntity);
                Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalEntityTargetAttack(goalType,entity,pathfinder,targetEntity);
                break;
            case ATTACK_CLOSE:
                Constrain.checkEntity(targetEntity);
                Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalEntityTargetAttackClose(goalType,entity,pathfinder);
                break;
            case DEFEND_ENTITY:
                Constrain.checkEntity(targetEntity);
                Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalEntityTargetDefend(goalType,entity,pathfinder,targetEntity);
                break;
            case GOTO_LOCATION:
                Constrain.checkTargetLocation(targetLocation);
                Constrain.checkSameWorld(targetLocation,entity.getLocation().getWorld());
                goal = new GoalLocationTargetGoto(goalType,entity,pathfinder,targetLocation);
                break;
            case FOLLOW_CHECKPOINTS:
                Constrain.checkCheckpoints(checkpoints);
                Constrain.checkSameWorld(checkpoints,entity.getLocation().getWorld());
                goal = new GoalLocationTargetFollowCheckpoints(goalType,entity,pathfinder,checkpoints,loop);
                break;
            case FOLLOW_CHECKPOINTS_WINGED:
                Constrain.checkCheckpoints(checkpoints);
                Constrain.checkSameWorld(checkpoints,entity.getLocation().getWorld());
                goal = new GoalLocationTargetFollowCheckpointsWingedFlight(goalType,entity,pathfinder,checkpoints,loop);
                break;
            case RANDOM_CHECKPOINTS:
                Constrain.checkCheckpoints(checkpoints);
                Constrain.checkSameWorld(checkpoints,entity.getLocation().getWorld());
                goal = new GoalLocationTargetRandomCheckpoints(goalType,entity,pathfinder,checkpoints);
                break;
            case HOLD_POSITION:
                Constrain.checkTargetLocation(targetLocation);
                Constrain.checkSameWorld(targetLocation,entity.getLocation().getWorld());
                goal = new GoalHoldPosition(goalType,entity,targetLocation);
                break;
            default:
                goal = null;
        }
        return goal;
    }
}
