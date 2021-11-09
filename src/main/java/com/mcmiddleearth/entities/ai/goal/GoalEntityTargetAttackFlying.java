package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWaypointTarget;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalEntityTargetChangedEvent;
import com.mcmiddleearth.entities.events.events.goal.GoalVirtualEntityIsClose;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class GoalEntityTargetAttackFlying extends GoalPath {

    protected McmeEntity target;
    protected boolean targetIncomplete = false;

    public GoalEntityTargetAttackFlying(VirtualEntity entity, VirtualEntityGoalFactory factory, Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
        this.target = factory.getTargetEntity();
        if(this.target instanceof Placeholder) {
            targetIncomplete = true;
        }
        setDefaultHeadGoal();
    }

    @Override
    public void doTick() {
        super.doTick();
        if (!targetIncomplete) {
            //if(getPath()!=null) Logger.getGlobal().info("Path: \n"+getPath().getStart()+" \n"+getPath().getEnd()+" \n"+getPath().getTarget());
            if (isCloseToTarget(GoalDistance.ATTACK/200)) {
                //Logger.getGlobal().info("delete path as entity is close.");
                EntitiesPlugin.getEntityServer().handleEvent(new GoalVirtualEntityIsClose(getEntity(), this));
                /*setIsMoving(false);//deletePath();
                movementSpeed = MovementSpeed.STAND;
                Location orientation = getEntity().getLocation().clone().setDirection(getTarget().getLocation().toVector()
                        .subtract(getEntity().getLocation().toVector()));
                setYaw(orientation.getYaw());
                setPitch(orientation.getPitch());*/
                if (!isFinished()) {
                    getEntity().attack(target);
//Logger.getGlobal().info("ATTACK! "+target.getName());
                }
                //}
            /*} else {
                setIsMoving(true);
                movementSpeed = MovementSpeed.WALK;*/
            }
            if (target.isDead()) {
                setFinished();
            }
        }
    }

    @Override
    public void update() {
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
            Vector tempTarget = getTarget().getLocation().toVector();
            Vector loc = new Vector(0,0,0);
            if(getEntity() instanceof WingedFlightEntity) {
                loc.subtract(((WingedFlightEntity)getEntity()).getAttackPoint());
            }
            if(!isInAttackPosition()) {
                tempTarget.add(new Vector(0,15,0).add(loc));
            } else {
                tempTarget.add(new Vector(0,target.getLocation().getY()-(getEntity().getLocation().getY()+loc.getY())+3,0));
            }
            setPathTarget(tempTarget);
        } else {
            setPathTarget(null);
            deletePath();
        }
        super.update();
    }

    private boolean isInAttackPosition() {
        Vector vec = target.getLocation().toVector().subtract(getEntity().getLocation().toVector());
        Location loc = getEntity().getLocation().clone().setDirection(vec);
        float currentYaw = (getEntity() instanceof CompositeEntity?((CompositeEntity)getEntity()).getCurrentYaw():getEntity().getYaw());
//Logger.getGlobal().info("entity: "+currentYaw+" target: "+loc.getYaw()+ " vec: "+vec);
        float diff = currentYaw-loc.getYaw();
        while(diff>180) diff -= 360;
        while(diff<-180) diff += 360;
//Logger.getGlobal().info("Diff: "+diff+ " dist: "+vec.lengthSquared()+" result: "+(Math.abs(diff) < 40 && vec.lengthSquared() < 1000));
        double dist = vec.lengthSquared();
        return Math.abs(diff) < 40 && dist < 1000 && dist > 1;
    }

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

    public boolean isCloseToTarget(double distanceSquared) {
        if(target!=null) {
            double distance = getEntity().getLocation().toVector().distanceSquared(getTarget().getLocation().toVector());
            if((getEntity() instanceof WingedFlightEntity)
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
        //addHeadGoal(new HeadGoalEntityTarget(this, 10));
        addHeadGoal(new HeadGoalWaypointTarget(this, 10));
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public VirtualEntityGoalFactory getFactory() {
        return super.getFactory().withTargetEntity(target);
    }
}