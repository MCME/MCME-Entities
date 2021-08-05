package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalCheckpointReachedEvent;
import org.bukkit.Location;

public class GoalLocationTargetRandomCheckpoints extends GoalLocationTarget {

    private final Location[] checkpoints;

    public GoalLocationTargetRandomCheckpoints(GoalType type, VirtualEntity entity, Pathfinder pathfinder,
                                               Location[] checkpoints) {
        super(type, entity, pathfinder, checkpoints[0]);
        this.checkpoints = checkpoints;
        setPathTarget(checkpoints[0].toVector());
        movementSpeed = MovementSpeed.WALK;
    }

    @Override
    public void update() {
        if(isCloseToTarget(GoalDistance.POINT)) {
            EntitiesPlugin.getEntityServer().handleEvent(new GoalCheckpointReachedEvent(getEntity(),this));
            deletePath();
            int nextCheckpoint = random.nextInt(checkpoints.length);
            setTarget(checkpoints[nextCheckpoint]);
            setPathTarget(checkpoints[nextCheckpoint].toVector());
        }
        super.update();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

}
