package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalEntityTarget;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWaypointTarget;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalEntityTargetChangedEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class GoalEntityTarget extends GoalPath {

    McmeEntity target;

    //private float headYaw, headPitch;

    public GoalEntityTarget(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder);
        this.target = target;
        setDefaultHeadGoal();
    }

    /*@Override
    public void doTick() {
        super.doTick();
        Location targetDir = getEntity().getLocation().clone()
                .setDirection(target.getLocation().toVector()
                        .subtract(getEntity().getLocation().toVector()));
        headYaw = targetDir.getYaw();
        headPitch = targetDir.getPitch();
    }*/

    @Override
    public void update() {
        if(target!=null) {
            setPathTarget(getTarget().getLocation().toVector());
        } else {
            setPathTarget(null);
            deletePath();
        }
        super.update();
    }

    /*@Override
    public boolean hasHeadRotation() {
        return true;
    }

    @Override
    public float getHeadYaw() {
        return headYaw;
    }

    @Override
    public float getHeadPitch() {
        return headPitch;
    }*/

    public McmeEntity getTarget() {
        return target;
    }

    public void setTarget(McmeEntity target) {
        if(this.target != target) {
            GoalEntityTargetChangedEvent event = new GoalEntityTargetChangedEvent(getEntity(),this,target);
            EntitiesPlugin.getEntityServer().handleEvent(event);
            if(!event.isCancelled()) {
                this.target = event.getNextTarget();
            }
        }
    }

    public boolean isCloseToTarget(double distanceSquared) {
        if(target!=null) {
            return getEntity().getLocation().toVector().distanceSquared(getTarget().getLocation().toVector()) < distanceSquared;
        } else {
            return false;
        }
    }

    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalEntityTarget(this, 10));
        addHeadGoal(new HeadGoalWaypointTarget(this, 10));
    }

    @Override
    public float getYaw() {
        return getEntity().getLocation().clone()
                .setDirection(target.getLocation().toVector().subtract(getEntity().getLocation().toVector()))
                .getYaw();
    }

    @Override
    public float getPitch() {
        return getEntity().getLocation().clone()
                .setDirection(target.getLocation().toVector().subtract(getEntity().getLocation().toVector()))
                .getPitch();
    }

    @Override
    public float getRoll() {
        return 0;
    }
}
