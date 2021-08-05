package com.mcmiddleearth.entities.events.events.goal;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public class GoalVirtualEntityIsClose extends GoalVirtualEntityEvent {

    public GoalVirtualEntityIsClose(VirtualEntity entity, Goal goal) {
        super(entity, goal);
    }

}
