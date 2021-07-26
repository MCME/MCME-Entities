package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class GoalLocationTargetFollowCheckpoints extends GoalLocationTarget {

    private final Location[] checkpoints;

    private int currentCheckpoint;

    private final boolean loop;

    private boolean isFinished;

    public GoalLocationTargetFollowCheckpoints(GoalType type, VirtualEntity entity, Pathfinder pathfinder,
                                               Location[] checkpoints, boolean loop) {
        super(type, entity, pathfinder, checkpoints[0]);
        this.checkpoints = checkpoints;
        this.loop = loop;
        currentCheckpoint = 0;
        setPathTarget(checkpoints[0].toVector());
        isFinished = false;
    }

    @Override
    public void update() {
        if(isFinished) {
            return;
        }
        if(isCloseToTarget(GoalDistance.POINT)) {
            currentCheckpoint++;
            if(currentCheckpoint==checkpoints.length && loop) {
                currentCheckpoint = 0;
            }
            deletePath();
            if(currentCheckpoint<checkpoints.length) {
                setTarget(checkpoints[currentCheckpoint]);
                setPathTarget(checkpoints[currentCheckpoint].toVector());
            } else {
                isFinished = true;
            }
        }
        super.update();
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

}
