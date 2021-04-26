package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class GotoLocationGoal extends LocationTargetingGoal {

    public GotoLocationGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder, Location target) {
        super(type, entity, pathfinder, target);
    }

    @Override
    public void update() {

    }

    @Override
    public void doTick() {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
