package com.mcmiddleearth.entities.events.events.goal;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.events.Cancelable;

public class GoalChangedEvent extends GoalEvent implements Cancelable {

    private Goal nextGoal;

    private boolean isCancelled;

    private McmeEntity entity;

    public GoalChangedEvent(McmeEntity entity, Goal currentGoal, Goal nextGoal) {
        super(entity, currentGoal);
        this.nextGoal = nextGoal;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }

    public Goal getNextGoal() {
        return nextGoal;
    }

    public void setNextGoal(Goal nextGoal) {
        this.nextGoal = nextGoal;
    }
}
