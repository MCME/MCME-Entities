package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWaypointTarget;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalEntityTargetChangedEvent;
import com.mcmiddleearth.entities.events.events.goal.GoalVirtualEntityIsClose;
import com.mcmiddleearth.entities.util.RotationMatrix;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class GoalEntityTargetAttackWinged extends GoalPath {

    protected McmeEntity target;
    protected boolean targetIncomplete = false;
    protected WingedFlightEntity entity;

    private Vector attackPointShift = new Vector(0,0,0);

    private final double flightLevel;
    private final float attackPitch;
    private final double dive;

    public GoalEntityTargetAttackWinged(WingedFlightEntity entity, VirtualEntityGoalFactory factory, Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
        this.entity = entity;
        this.target = factory.getTargetEntity();
        if(this.target instanceof Placeholder) {
            targetIncomplete = true;
        }
        this.flightLevel = factory.getFlightLevel();
        this.attackPitch = factory.getAttackPitch();
        this.dive = factory.getDive();
        setDefaultHeadGoal();
    }

    @Override
    public void doTick() {
        super.doTick();
        if (!targetIncomplete) {
            //if(getPath()!=null) Logger.getGlobal().info("Path: \n"+getPath().getStart()+" \n"+getPath().getEnd()+" \n"+getPath().getTarget());
            if (isCloseToTarget(GoalDistance.ATTACK)) {
                //Logger.getGlobal().info("delete path as entity is close.");
                EntitiesPlugin.getEntityServer().handleEvent(new GoalVirtualEntityIsClose(getEntity(), this));
                /*setIsMoving(false);//deletePath();
                movementSpeed = MovementSpeed.STAND;
                Location orientation = getEntity().getLocation().clone().setDirection(getTarget().getLocation().toVector()
                        .subtract(getEntity().getLocation().toVector()));
                setYaw(orientation.getYaw());
                setPitch(orientation.getPitch());*/
                if (!isFinished()) {
//Logger.getGlobal().info("Attack Position: "+ getAttackLocation().toVector()+" Entity: "+getEntity().getLocation().toVector());
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
/*double distance = horizontalDistanceSquared();
double distancePivot = GoalDistance.ATTACK*(1+entity.getAttackPoint().length());
Logger.getGlobal().info("IsClose: "+isCloseToTarget(GoalDistance.ATTACK)+" <- "+distance+ " pivot: "+distancePivot);
 /*Logger.getGlobal().info("Is close: "+isCloseToTarget(GoalDistance.ATTACK)+" "
     +getAttackLocation().toVector().distanceSquared(getTarget().getLocation().toVector())
     +" "+GoalDistance.ATTACK*(1+entity.getAttackPoint().length()));*/
            updateAttackPointShift();
            Vector tempTarget = getTarget().getLocation().toVector();//.subtract(attackPointShift);
            //double attackY = getAttackY();
            if(!isInAttackPosition()) {
                tempTarget.add(new Vector(0,flightLevel,0));
//Logger.getGlobal().warning("         Raise! "+tempTarget);//getAttackLocation());
            } else {
                tempTarget.add(new Vector(0,(target.getLocation().getY()-entity.getLocation().getY())*dive,//1.15,
                                          0));
//Logger.getGlobal().severe("        Attack! "+tempTarget);//getAttackLocation());
            }
            setPathTarget(tempTarget);
        } else {
            setPathTarget(null);
            deletePath();
        }
        super.update();
    }

    /*private double getFlightLevel() {
        return 20*(1-entity.getAttackPoint().getY()/5);
    }*/

    /*private double getAttackY() {
        Vector loc = new Vector(0,0,0);
        if(getEntity() instanceof WingedFlightEntity) {
            loc.subtract(((WingedFlightEntity)getEntity()).getAttackPoint());
        }
        return loc.getY();
    }*/

    private boolean isInAttackPosition() {
        Vector vec = target.getLocation().toVector().subtract(entity.getLocation().toVector());
        Location loc = getEntity().getLocation().clone().setDirection(vec);
        float currentYaw = entity.getCurrentYaw();
//Logger.getGlobal().info("entity: "+currentYaw+" target: "+loc.getYaw()+ " vec: "+vec);
        float diff = currentYaw-loc.getYaw();
        while(diff>180) diff -= 360;
        while(diff<-180) diff += 360;
//Logger.getGlobal().info("Diff: "+diff+ " dist: "+vec.lengthSquared()+" result: "+(Math.abs(diff) < 40 && vec.lengthSquared() < 1000));
        double dist = vec.length();
        double pitch = Math.asin(-vec.getY()/dist);
        double pitchPivot = attackPitch/180d*Math.PI;
//Logger.getGlobal().info("Yaw: current: "+currentYaw+" aim: "+loc.getYaw()+" => "+diff+" < "+40);
//Logger.getGlobal().info("Pitch: "+dist+" "+pitch+" > "+pitchPivot);
//Logger.getGlobal().info("Height: "+(-vec.getY())+" > "+(flightLevel*0.6));
        return Math.abs(diff) < 40 && pitch > pitchPivot && vec.getY() < entity.getAttackPoint().getY() && dist > 5;
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
            double distance = getAttackLocation().toVector().distanceSquared(getTarget().getLocation().toVector());
            double distancePivot = distanceSquared*(1+entity.getAttackPoint().length());
            return distance < distancePivot;
        } else {
            return false;
        }
    }

    public double horizontalDistanceSquared() {
        Vector attack = getAttackLocation().toVector();
        Vector target = getTarget().getLocation().toVector();
        return (attack.getX()-target.getX())*(attack.getX()-target.getX())+(attack.getZ()-target.getZ())*(attack.getZ()-target.getZ());
    }

    private Location getAttackLocation() {
        Location loc = getEntity().getLocation().clone();
        loc.add(attackPointShift);
        return loc;
    }

    private void updateAttackPointShift() {
        Vector vector = (entity).getAttackPoint();
        //Vector rotatedZ = RotationMatrix.fastRotateZ(vector,-getEntity().getRoll());
        //Vector rotatedZX = RotationMatrix.fastRotateX(rotatedZ, getEntity().getPitch());
        attackPointShift = RotationMatrix.fastRotateY(vector,-entity.getCurrentYaw());
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