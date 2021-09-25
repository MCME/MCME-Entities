package com.mcmiddleearth.entities.events.events.goal;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.events.events.McmeEntityEvent;

public abstract class GoalEvent implements McmeEntityEvent {

    private final Goal goal;

    private final McmeEntity entity;

    public GoalEvent(McmeEntity entity, Goal goal) {
        this.entity = entity;
        this.goal = goal;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }

    public Goal getGoal() {
        return goal;
    }
}
