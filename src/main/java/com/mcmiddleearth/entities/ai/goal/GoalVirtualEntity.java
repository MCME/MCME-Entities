package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoal;
import com.mcmiddleearth.entities.ai.movement.MovementSpeed;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalCheckpointReachedEvent;
import com.mcmiddleearth.entities.events.events.goal.GoalFinishedEvent;
import com.mcmiddleearth.entities.events.events.goal.HeadGoalChangedEvent;
import jdk.javadoc.internal.doclets.formats.html.markup.Head;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public abstract class GoalVirtualEntity implements Goal{

    private final GoalType type;

    private final VirtualEntity entity;

    private final Set<HeadGoal> headGoals = new HashSet<>();
    private HeadGoal currentHeadGoal;
    private int headGoalTicks=0;
    private float currentDurationFactor=1;

    protected final static Random random = new Random();

    private int updateInterval = 10;

    private final int updateRandom;

    private boolean isFinished = false;

    protected MovementSpeed movementSpeed = MovementSpeed.STAND;

    public GoalVirtualEntity(GoalType type, VirtualEntity entity) {
        this.type = type;
        this.entity = entity;
        updateRandom = random.nextInt(updateInterval);
    }

    public abstract Vector getDirection();

    public abstract boolean hasRotation();

    public abstract float getRotation();

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

    public float getHeadPitch() {
        if(currentHeadGoal!=null) {
            return currentHeadGoal.getHeadPitch();
        } else {
            return 0;
        }
    }

    public void update(){
        if(currentHeadGoal==null || headGoals.size()>1 && headGoalTicks > currentHeadGoal.getDuration()*currentDurationFactor) {
            setRandomHeadGoal();
        }
    }

    public void doTick(){
        if(currentHeadGoal!=null) {
            currentHeadGoal.doTick();
        }
        headGoalTicks++;
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

}
