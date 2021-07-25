package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class GoalLocationTargetDefend extends GoalLocationTarget {

    public GoalLocationTargetDefend(GoalType type, VirtualEntity entity, Pathfinder pathfinder, Location target) {
        super(type, entity, pathfinder, target);
    }

    @Override
    public boolean isFinished() {
        return false;
    }


}
