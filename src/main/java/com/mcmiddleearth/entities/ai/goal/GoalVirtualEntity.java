package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoal;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalFinishedEvent;
import com.mcmiddleearth.entities.events.events.goal.HeadGoalChangedEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

public abstract class GoalVirtualEntity implements Goal {

    private final GoalType type;

    private final VirtualEntity entity;

    private float yaw, pitch, roll;
    private boolean hasRotation;

    private final Set<HeadGoal> headGoals = new HashSet<>();
    private HeadGoal currentHeadGoal;
    private int headGoalTicks=0;
    private float currentDurationFactor=1;

    protected final static Random random = new Random();

    private int updateInterval = 10;

    private final int updateRandom;

    private boolean isFinished = false;

    protected MovementSpeed movementSpeed;

    public GoalVirtualEntity(VirtualEntity entity, VirtualEntityGoalFactory factory) {
        this.type = factory.getGoalType();
        this.entity = entity;
        this.updateInterval = factory.getUpdateInterval();
        movementSpeed = factory.getMovementSpeed();
        updateRandom = random.nextInt(updateInterval);
    }

    @Override
    public abstract Vector getDirection();

    @Override
    public boolean hasRotation() {
        return hasRotation;
    }

    @Override
    public void resetRotationFlags() {
        hasRotation = false;
        if(currentHeadGoal!=null) {
            currentHeadGoal.resetRotationFlags();
        }
    }

    @Override
    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        hasRotation = true;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        hasRotation = true;
        this.pitch = pitch;
    }

    @Override
    public float getRoll() {
        hasRotation = true;
        return roll;
    }

    public void setRoll(float roll) {
        hasRotation = true;
        this.roll = roll;
    }

    public boolean hasHeadRotation(){
        return currentHeadGoal!=null && currentHeadGoal.hasHeadRotation();
    }

    public float getHeadYaw() {
        if(currentHeadGoal!=null) {
            return currentHeadGoal.getHeadYaw();
        } else {
            return 0;
        }
    }

    @Override
    public float getHeadPitch() {
        if(currentHeadGoal!=null) {
//Logger.getGlobal().info("Virtual goal head rotation: "+ currentHeadGoal.getHeadYaw()+" "+currentHeadGoal.getHeadPitch());
            return currentHeadGoal.getHeadPitch();
        } else {
            return 0;
        }
    }

    @Override
    public void update(){
        if(currentHeadGoal==null || headGoals.size()>1 && headGoalTicks > currentHeadGoal.getDuration()*currentDurationFactor) {
            setRandomHeadGoal();
        }
    }

    @Override
    public void doTick(){
        if(currentHeadGoal!=null) {
            currentHeadGoal.doTick();
        }
        headGoalTicks++;
    }

    public boolean isForceTeleport() {
        return false;
    }
    @Override
    public MovementSpeed getMovementSpeed() {
        return movementSpeed;
    }

    public void addHeadGoal(HeadGoal headGoal) {
        headGoals.add(headGoal);
        //setRandomHeadGoal();
    }

    public void removeHeadGoal(HeadGoal headGoal) {
        headGoals.remove(headGoal);
        if(headGoals.isEmpty()) {
            EntitiesPlugin.getEntityServer().handleEvent(new HeadGoalChangedEvent(getEntity(), this, null));
            currentHeadGoal = null;
        } else if(currentHeadGoal == headGoal){
            setRandomHeadGoal();
        }
    }

    public void clearHeadGoals() {
        EntitiesPlugin.getEntityServer().handleEvent(new HeadGoalChangedEvent(getEntity(), this, null));
        headGoals.clear();
        currentHeadGoal = null;
    }

    public HeadGoal getCurrentHeadGoal() {
        return currentHeadGoal;
    }

    public Set<HeadGoal> getHeadGoals() {
        return new HashSet<>(headGoals);
    }

    public void setDefaultHeadGoal() {}

    private void setRandomHeadGoal() {
        if(headGoals.size()>0) {
            currentDurationFactor = new Random().nextFloat()+0.7f;
            HeadGoal nextHeadGoal = (HeadGoal) headGoals.toArray()[random.nextInt(headGoals.size())];
            if(nextHeadGoal!=currentHeadGoal) {
                EntitiesPlugin.getEntityServer().handleEvent(new HeadGoalChangedEvent(getEntity(), this, nextHeadGoal));
                currentHeadGoal = nextHeadGoal;
            }
        }
        headGoalTicks = 0;
    }

    public VirtualEntity getEntity() {
        return entity;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    @Override
    public int getUpdateRandom() {
        return updateRandom;
    }

    public void setFinished() {
        if(!isFinished) {
            GoalFinishedEvent event = new GoalFinishedEvent(getEntity(), this);
            EntitiesPlugin.getEntityServer().handleEvent(event);
            isFinished = true;
        }
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    public VirtualEntityGoalFactory getFactory() {
        VirtualEntityGoalFactory factory = new VirtualEntityGoalFactory(type)
                .withTargetEntity(entity)
                .withHeadGoals(headGoals)
                .withUpdateInterval(updateInterval)
                .withMovementSpeed(movementSpeed);
        return factory;
    }

    @Override
    public GoalType getType() {
        return type;
    }
}
