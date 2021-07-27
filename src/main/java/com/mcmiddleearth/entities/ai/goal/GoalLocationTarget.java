package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalEntityTarget;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalLocationTarget;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWaypointTarget;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalLocationTargetChangedEvent;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class GoalLocationTarget extends GoalPath {

    private Location target;

    public GoalLocationTarget(GoalType type, VirtualEntity entity, Pathfinder pathfinder, Location target) {
        super(type, entity, pathfinder);
        this.target = target;
        setDefaultHeadGoal();
    }

    @Override
    public void findPath(Vector start) {
        super.findPath(start);
    }

    /*@Override
    public boolean hasHeadRotation() {
        return false;
    }

    @Override
    public float getHeadYaw() {
        return 0;
    }

    @Override
    public float getHeadPitch() {
        return 0;
    }

    @Override
    public boolean hasRotation() {
        return true;
    }*/

    @Override
    public float getRotation() {
        return getEntity().getLocation().clone()
                .setDirection(target.toVector().subtract(getEntity().getLocation().toVector()))
                .getYaw();
    }

    public Location getTarget() {
        return target;
    }

    public void setTarget(Location target) {
        if(!this.target.equals(target)) {
            GoalLocationTargetChangedEvent event = new GoalLocationTargetChangedEvent(getEntity(),this,target);
            EntitiesPlugin.getEntityServer().handleEvent(event);
            if(!event.isCancelled()) {
                this.target = event.getNextTarget();
            }
        }
    }

    public boolean isCloseToTarget(double distanceSquared) {
        return getEntity().getLocation().toVector().distanceSquared(getTarget().toVector()) < distanceSquared;
    }

    @Override
    public void update() {
        //if(!isCloseToTarget(GoalDistance.POINT)) {
        setPathTarget(target.toVector());
        //}
        super.update();
    }

    @Override
    public void doTick() {
        setIsMoving(!isCloseToTarget(GoalDistance.POINT));
        super.doTick();
    }

    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalLocationTarget(this, 10));
        addHeadGoal(new HeadGoalWaypointTarget(this, 40));
    }
}
