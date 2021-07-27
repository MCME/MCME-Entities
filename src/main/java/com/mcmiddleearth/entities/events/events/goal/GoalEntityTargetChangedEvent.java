package com.mcmiddleearth.entities.events.events.goal;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.Cancelable;

public class GoalEntityTargetChangedEvent extends GoalVirtualEntityEvent implements Cancelable {

    private McmeEntity nextTarget;

    private boolean isCancelled;

    public GoalEntityTargetChangedEvent(VirtualEntity entity, Goal goal, McmeEntity nextTarget) {
        super(entity, goal);
        this.nextTarget = nextTarget;
    }

    public McmeEntity getNextTarget() {
        return nextTarget;
    }

    public void setNextTarget(McmeEntity nextTarget) {
        this.nextTarget = nextTarget;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
