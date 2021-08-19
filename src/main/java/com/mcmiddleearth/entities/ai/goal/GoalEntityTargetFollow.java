package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalVirtualEntityIsClose;
import org.bukkit.Location;

public class GoalEntityTargetFollow extends GoalEntityTarget {

    private double followDistance = GoalDistance.TALK;

    public GoalEntityTargetFollow(VirtualEntity entity, VirtualEntityGoalFactory factory, Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
    }

    @Override
    public void doTick() {
        super.doTick();
        if(!targetIncomplete) {
            if (isCloseToTarget(followDistance)) {
                //Logger.getGlobal().info("delete path as entity is close.");
                EntitiesPlugin.getEntityServer().handleEvent(new GoalVirtualEntityIsClose(getEntity(), this));
                setIsMoving(false);//deletePath();
                Location orientation = getEntity().getLocation().clone().setDirection(getTarget().getLocation().toVector()
                        .subtract(getEntity().getLocation().toVector()));
                setYaw(orientation.getYaw());
                setPitch(orientation.getPitch());
            } else {
                setIsMoving(true);
            }
        }
    }

    /*@Override
    public void update() {
        if(!(isCloseToTarget(GoalDistance.CAUTION))) {
            super.update();
        }
    }*/

    public void setFollowDistance(double distance) {
        this.followDistance = distance;
    }

}
