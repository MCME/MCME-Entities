package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalVirtualEntityIsClose;
import org.bukkit.Location;

public class GoalEntityTargetFollowStalking extends GoalEntityTargetFollow {

    public GoalEntityTargetFollowStalking(VirtualEntity entity, VirtualEntityGoalFactory factory, Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
        setFollowDistance(GoalDistance.STALKING);
    }


}
