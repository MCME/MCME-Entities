package com.mcmiddleearth.entities.events.events.goal;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.Cancelable;
import org.bukkit.Location;

public class GoalLocationTargetChangedEvent extends GoalVirtualEntityEvent implements Cancelable {

    private Location nextTarget;

    private boolean isCancelled;

    public GoalLocationTargetChangedEvent(VirtualEntity entity, Goal goal, Location nextTarget) {
        super(entity, goal);
        this.nextTarget = nextTarget;
    }

    public Location getNextTarget() {
        return nextTarget;
    }

    public void setNextTarget(Location nextTarget) {
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
