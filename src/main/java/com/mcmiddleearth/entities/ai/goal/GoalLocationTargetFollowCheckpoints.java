package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalCheckpointReachedEvent;
import com.mcmiddleearth.entities.events.events.goal.GoalVirtualEntityIsClose;
import org.bukkit.Location;

public class GoalLocationTargetFollowCheckpoints extends GoalLocationTarget {

    private final Location[] checkpoints;

    private int currentCheckpoint;

    private final boolean loop;

    public GoalLocationTargetFollowCheckpoints(GoalType type, VirtualEntity entity, Pathfinder pathfinder,
                                               Location[] checkpoints, boolean loop) {
        super(type, entity, pathfinder, checkpoints[0]);
        this.checkpoints = checkpoints;
        this.loop = loop;
        currentCheckpoint = 0;
        setPathTarget(checkpoints[0].toVector());
    }

    @Override
    public void update() {
        if(isFinished()) {
            return;
        }
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
            }
        }
        super.update();
    }

}
