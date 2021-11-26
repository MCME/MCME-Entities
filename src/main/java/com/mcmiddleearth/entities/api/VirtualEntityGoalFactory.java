package com.mcmiddleearth.entities.api;

import com.mcmiddleearth.entities.ai.goal.*;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoal;
import com.mcmiddleearth.entities.ai.pathfinding.FlyingPathfinder;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.ai.pathfinding.SimplePathfinder;
import com.mcmiddleearth.entities.ai.pathfinding.WalkingPathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.util.Constrain;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VirtualEntityGoalFactory {

    private MovementSpeed movementSpeed = MovementSpeed.WALK;

    private GoalType goalType = GoalType.NONE;

    private Location targetLocation = null;

    private Location[] checkpoints = null;

    private int startCheckpoint = 0;

    private McmeEntity targetEntity = null;

    private boolean loop;

    private Set<HeadGoal> headGoals = null;

    private int updateInterval = 10;

    private Vector relativePosition = new Vector(0d,0d,0d);

    private double flightLevel = 20;
    private float attackPitch = 45;
    private double dive = 1.15;

    private boolean writeDefaultsToFile = false;

    public VirtualEntityGoalFactory(GoalType goalType) {
        this.goalType = goalType;
    }

    public VirtualEntityGoalFactory withTargetLocation(Location target) {
        this.targetLocation = target;
        return this;
    }

    public static Collection<String> availableProperties() {
        return Stream.of("goaltype","targetlocation","targetentity","loop","checkpoints","headgoal").map(String::toLowerCase)
                .sorted().collect(Collectors.toList());
    }


    public Location getTargetLocation() {
        return targetLocation;
    }

    public VirtualEntityGoalFactory withTargetEntity(McmeEntity target) {
        this.targetEntity = target;
        return this;
    }

    public McmeEntity getTargetEntity() {
        return targetEntity;
    }

    public VirtualEntityGoalFactory withGoalType(GoalType goalType) {
        this.goalType = goalType;
        return this;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public VirtualEntityGoalFactory withCheckpoints(Location[] checkpoints) {
        this.checkpoints = checkpoints;
        return this;
    }

    public Location[] getCheckpoints() {
        return checkpoints;
    }

    public int getStartCheckpoint() {
        return startCheckpoint;
    }

    public VirtualEntityGoalFactory withStartCheckpoint(int startCheckpoint) {
        this.startCheckpoint = startCheckpoint;
        return this;
    }

    public VirtualEntityGoalFactory withLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public boolean isLoop() {
        return loop;
    }

    public MovementSpeed getMovementSpeed() {
        return movementSpeed;
    }

    public VirtualEntityGoalFactory withMovementSpeed(MovementSpeed movementSpeed) {
        this.movementSpeed = movementSpeed;
        return this;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public VirtualEntityGoalFactory withUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    public Set<HeadGoal> getHeadGoals() {
        return headGoals;
    }

    public VirtualEntityGoalFactory withHeadGoals(Set<HeadGoal> headGoals) {
        this.headGoals = headGoals;
        return this;
    }

    public Vector getRelativePosition() {
        return relativePosition;
    }

    public VirtualEntityGoalFactory withRelativePosition(Vector relativePosition) {
        this.relativePosition = relativePosition;
        return this;
    }

    public double getFlightLevel() {
        return flightLevel;
    }

    public VirtualEntityGoalFactory withFlightLevel(double flightLevel) {
        this.flightLevel = flightLevel;
        return this;
    }

    public float getAttackPitch() {
        return attackPitch;
    }

    public VirtualEntityGoalFactory withAttackPitch(float attackPitch) {
        this.attackPitch = attackPitch;
        return this;
    }

    public double getDive() {
        return dive;
    }

    public VirtualEntityGoalFactory withDive(double dive) {
        this.dive = dive;
        return this;
    }

    public boolean isWriteDefaultsToFile() {
        return writeDefaultsToFile;
    }

    public VirtualEntityGoalFactory withWriteDefaultsToFile(boolean writeDefaultsToFile) {
        this.writeDefaultsToFile = writeDefaultsToFile;
        return this;
    }

    public static VirtualEntityGoalFactory getDefaults() {
        return new VirtualEntityGoalFactory(GoalType.NONE);
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
        if((entity instanceof WingedFlightEntity) && movementType.equals(MovementType.FLYING)) {
Logger.getGlobal().info("pathfinder: flying");
            pathfinder = new FlyingPathfinder((WingedFlightEntity)entity);
        } else {
            switch (movementType) {
                case UPRIGHT:
                case SNEAKING:
                    pathfinder = new WalkingPathfinder(entity);
                    //Logger.getGlobal().info("walking pathfinding!");
                    break;
                default:
                    //Logger.getGlobal().info("Simple pathfinding!");
                    pathfinder = new SimplePathfinder();
            }
        }
        switch(goalType) {
            case WATCH_ENTITY:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalWatchEntity(entity,this);
                break;
            case RANGED_ATTACK_ENTITY:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalRangedAttack(entity,this);
                break;
            case FOLLOW_ENTITY:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalEntityTargetFollow(entity,this, pathfinder);
                break;
            case FOLLOW_ENTITY_STALKING:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalEntityTargetFollowStalking(entity,this, pathfinder);
                break;
            case ATTACK_ENTITY:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalEntityTargetAttack(entity,this, pathfinder);
                break;
            case ATTACK_ENTITY_SPRINT:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalEntityTargetAttackSprint(entity,this, pathfinder);
                break;
            case ATTACK_ENTITY_WINGED:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                Constrain.checkClass(entity, WingedFlightEntity.class);
                goal = new GoalEntityTargetAttackWinged((WingedFlightEntity)entity,this, pathfinder);
                break;
            case ATTACK_CLOSE:
                if(targetEntity != null && !(targetEntity instanceof Placeholder)) {
                    Constrain.checkSameWorld(targetEntity.getLocation(), entity.getLocation().getWorld());
                }
                goal = new GoalEntityTargetAttackClose(entity, this, pathfinder);
                break;
            case DEFEND_ENTITY:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalEntityTargetDefend(entity,this, pathfinder);
                break;
            case GOTO_LOCATION:
                Constrain.checkTargetLocation(targetLocation);
                Constrain.checkSameWorld(targetLocation,entity.getLocation().getWorld());
                goal = new GoalLocationTargetGoto(entity,this, pathfinder);
                break;
            case FOLLOW_CHECKPOINTS:
                Constrain.checkCheckpoints(checkpoints);
                Constrain.checkSameWorld(checkpoints,entity.getLocation().getWorld());
                goal = new GoalLocationTargetFollowCheckpoints(entity,this, pathfinder);
                break;
            /*case FOLLOW_CHECKPOINTS_WINGED:
                Constrain.checkCheckpoints(checkpoints);
                Constrain.checkSameWorld(checkpoints,entity.getLocation().getWorld());
                goal = new GoalLocationTargetFollowCheckpointsWingedFlight(entity,this, pathfinder);
                break;*/
            case RANDOM_CHECKPOINTS:
                Constrain.checkCheckpoints(checkpoints);
                Constrain.checkSameWorld(checkpoints,entity.getLocation().getWorld());
                goal = new GoalLocationTargetRandomCheckpoints(entity,this, pathfinder);
                break;
            case HOLD_POSITION:
                Constrain.checkTargetLocation(targetLocation);
                Constrain.checkSameWorld(targetLocation,entity.getLocation().getWorld());
                goal = new GoalHoldPosition(entity,this);
                break;
            case MIMIC:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalMimic(entity,this);
                break;
            case JOCKEY:
                Constrain.checkEntity(targetEntity);
                if(!(targetEntity instanceof Placeholder))
                    Constrain.checkSameWorld(targetEntity.getLocation(),entity.getLocation().getWorld());
                goal = new GoalJockey(entity,this);
                break;
            case RIDING_PLAYER:
Logger.getGlobal().info("Target: "+targetEntity);
Logger.getGlobal().info("entity: "+entity);
                if((targetEntity instanceof RealPlayer) && entity instanceof WingedFlightEntity) {
                    Constrain.checkSameWorld(targetEntity.getLocation(), entity.getLocation().getWorld());
Logger.getGlobal().info("Create goal!");
                    goal = new GoalRidingPlayer((WingedFlightEntity) entity, this);
                } else {
                    goal = null;
                }
                break;
            default:
                goal = null;
        }
        if(goal!=null && headGoals != null) {
            goal.clearHeadGoals();
            headGoals.forEach(headGoal -> {
                if(headGoal.provideGoalAndEntity(goal,entity)) {
                    goal.addHeadGoal(headGoal);
                }
            });
        }
        return goal;
    }


}
