package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalCheckpointReachedEvent;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.logging.Logger;

public class GoalLocationTargetFollowCheckpoints extends GoalLocationTarget {

    private final Location[] checkpoints;

    private int currentCheckpoint;

    private final boolean loop;

    public GoalLocationTargetFollowCheckpoints(VirtualEntity entity, VirtualEntityGoalFactory factory, Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
        this.checkpoints = factory.getCheckpoints();
//Arrays.stream(checkpoints).forEach(check-> Logger.getGlobal().info("* "+check));
        this.loop = factory.isLoop();
        currentCheckpoint = factory.getStartCheckpoint();
        setTarget(checkpoints[currentCheckpoint]);
        setPathTarget(checkpoints[currentCheckpoint].toVector());
    }

    @Override
    public void update() {
        if(isFinished()) {
            return;
        }
//Logger.getGlobal().info("ent "+getEntity().getLocation().toVector());
//Logger.getGlobal().info("tar" +getTarget().toVector());
//if(getPath() != null) Logger.getGlobal().info("pat "+getPath().getEnd());

//Logger.getGlobal().info("distance: "+getEntity().getLocation().toVector().distanceSquared(getTarget().toVector()));
        if(isCloseToTarget(GoalDistance.POINT)) {
            EntitiesPlugin.getEntityServer().handleEvent(new GoalCheckpointReachedEvent(getEntity(),this));
            currentCheckpoint++;
            if(currentCheckpoint==checkpoints.length && loop) {
                currentCheckpoint = 0;
            }
            deletePath();
            if(currentCheckpoint<checkpoints.length) {
                setTarget(checkpoints[currentCheckpoint]);
                setPathTarget(checkpoints[currentCheckpoint].toVector());
            } else {
                setFinished();
                movementSpeed = MovementSpeed.STAND;
            }
        }
        super.update();
    }

    @Override
    public VirtualEntityGoalFactory getFactory() {
        return super.getFactory()
                .withStartCheckpoint(Math.min(currentCheckpoint,checkpoints.length-1))
                .withCheckpoints(checkpoints)
                .withLoop(loop);
    }
}
