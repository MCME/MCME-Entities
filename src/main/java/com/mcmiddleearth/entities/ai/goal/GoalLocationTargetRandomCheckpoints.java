package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalCheckpointReachedEvent;
import org.bukkit.Location;

public class GoalLocationTargetRandomCheckpoints extends GoalLocationTarget {

    private final Location[] checkpoints;

    public GoalLocationTargetRandomCheckpoints(VirtualEntity entity, VirtualEntityGoalFactory factory,
                                               Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
        this.checkpoints = factory.getCheckpoints();
        setTarget(checkpoints[factory.getStartCheckpoint()]);
        setPathTarget(checkpoints[random.nextInt(checkpoints.length)].toVector());
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

    @Override
    public VirtualEntityGoalFactory getFactory() {
        VirtualEntityGoalFactory factory = super.getFactory()
                .withCheckpoints(checkpoints);
        return factory;
    }

}
