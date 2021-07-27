package com.mcmiddleearth.entities.events.events.goal;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public abstract class GoalVirtualEntityEvent extends GoalEvent {

    private VirtualEntity virtualEntity;

    public GoalVirtualEntityEvent(VirtualEntity entity, Goal goal) {
        super(entity, goal);
        this.virtualEntity = entity;
    }

    public VirtualEntity getVirtualEntity() {
        return virtualEntity;
    }
}
