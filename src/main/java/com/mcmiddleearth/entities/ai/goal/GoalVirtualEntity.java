package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.goal.head.HeadGoal;
import com.mcmiddleearth.entities.entities.VirtualEntity;
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

    public GoalVirtualEntity(GoalType type, VirtualEntity entity) {
        this.type = type;
        this.entity = entity;
        updateRandom = random.nextInt(updateInterval);
    }

    public abstract Vector getDirection();

    public abstract boolean hasRotation();

    public abstract float getRotation();

    public boolean hasHeadRotation(){
        return currentHeadGoal!=null;
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
        if(headGoals.size()>1 && headGoalTicks > currentHeadGoal.getDuration()*currentDurationFactor) {
            setRandomHeadGoal();
        }
    }

    public void doTick(){
        currentHeadGoal.doTick();
        headGoalTicks++;
    }

    public void addHeadGoal(HeadGoal headGoal) {
        headGoals.add(headGoal);
        setRandomHeadGoal();
    }

    public void removeHeadGoal(HeadGoal headGoal) {
        headGoals.remove(headGoal);
        if(headGoals.isEmpty()) {
            currentHeadGoal = null;
        } else {
            setRandomHeadGoal();
        }
    }

    public void clearHeadGoals() {
        headGoals.clear();
        currentHeadGoal = null;
    }

    public Set<HeadGoal> getHeadGoals() {
        return new HashSet<>(headGoals);
    }

    public void setDefaultHeadGoal() {}

    private void setRandomHeadGoal() {
        currentDurationFactor = new Random().nextFloat()+0.7f;
        currentHeadGoal = (HeadGoal) headGoals.toArray()[random.nextInt(headGoals.size())];
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


}
