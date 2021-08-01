package com.mcmiddleearth.entities.events.events.goal;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public class GoalCheckpointReachedEvent extends GoalVirtualEntityEvent {

    public GoalCheckpointReachedEvent(VirtualEntity entity, Goal goal) {
        super(entity, goal);
    }
}