package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalEntityTarget;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWaypointTarget;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalEntityTargetChangedEvent;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public abstract class GoalEntityTarget extends GoalPath {

    protected McmeEntity target;
    protected boolean targetIncomplete = false;

    //private float headYaw, headPitch;

    public GoalEntityTarget(VirtualEntity entity, VirtualEntityGoalFactory factory, Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
        this.target = factory.getTargetEntity();
        if(this.target instanceof Placeholder) {
            targetIncomplete = true;
        }
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
        if(!targetIncomplete) {
            if (!target.isOnline()) {
                targetIncomplete = true;
            } else {
                if(target.isDead()) setFinished();
            }
        }
        if(targetIncomplete) {
//Logger.getGlobal().info("Incomplete, searching for: "+target.getUniqueId());
            McmeEntity search = EntitiesPlugin.getEntityServer().getEntity(target.getUniqueId());
//Logger.getGlobal().info("Completition: "+search);
            if(search != null) {
                target = search;
                targetIncomplete = false;
            }
        }
        if(target!=null && !targetIncomplete) {
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
                if(this.target instanceof Placeholder) {
                    targetIncomplete = true;
                }
            }
        }
    }

    /*@Override
    public boolean isCloseToTarget(double distanceSquared) {
        double distance = getEntity().getLocation().distanceSquared(getTarget().getLocation());
//Logger.getGlobal().info("Distance: "+distance);
        return distance < distanceSquared*400;
    }*/

    public boolean isCloseToTarget(double distanceSquared) {
        if(target!=null) {
            double distance = getEntity().getLocation().toVector().distanceSquared(getTarget().getLocation().toVector());
            if((getEntity() instanceof  WingedFlightEntity)
                    && (getEntity().getMovementType().equals(MovementType.FLYING)
                        || getEntity().getMovementType().equals(MovementType.GLIDING))) {
                return distance < (distanceSquared*400);
            } else {
                return distance < distanceSquared;
            }
        } else {
            return false;
        }
    }

    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalEntityTarget(this, 10));
        addHeadGoal(new HeadGoalWaypointTarget(this, 10));
    }

    /*remove
    @Override
    public float getYaw() {
        return getEntity().getLocation().clone()
                .setDirection(target.getLocation().toVector().subtract(getEntity().getLocation().toVector()))
                .getYaw();
    }

    remove
    @Override
    public float getPitch() {
        return getEntity().getLocation().clone()
                .setDirection(target.getLocation().toVector().subtract(getEntity().getLocation().toVector()))
                .getPitch();
    }*/

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public VirtualEntityGoalFactory getFactory() {
        return super.getFactory().withTargetEntity(target);
    }
}
