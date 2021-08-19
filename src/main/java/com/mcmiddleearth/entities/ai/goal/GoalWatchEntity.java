package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWatch;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class GoalWatchEntity extends GoalVirtualEntity {

    private McmeEntity target;
    private boolean targetIncomplete = false;

    //private boolean hasRotation;
    //private float rotation;

    private int tickCounter = 0;

    public GoalWatchEntity(VirtualEntity entity, VirtualEntityGoalFactory factory) {
        super(entity, factory);
        this.target = factory.getTargetEntity();
        if(this.target instanceof Placeholder) {
            targetIncomplete = true;
        }
        movementSpeed = MovementSpeed.STAND;
        setDefaultHeadGoal();
    }

    @Override
    public void update() {
        super.update();
        if(targetIncomplete) {
            McmeEntity search = EntitiesPlugin.getEntityServer().getEntity(target.getUniqueId());
            if(search != null) {
                target = search;
                targetIncomplete = false;
            }
        }
        if(!targetIncomplete) {
            if (tickCounter%3==0) {
                Location orientation = getEntity().getLocation().clone()
                        .setDirection(target.getLocation().toVector().subtract(getEntity().getLocation().toVector()));
                setYaw(orientation.getYaw());
                setPitch(orientation.getPitch());
                tickCounter = 0;
                //hasRotation = true;
            }
            //secondUpdate = !secondUpdate;
            tickCounter++;
        }
    }

    /*@Override
    public void doTick() {
        super.doTick();

        //hasRotation = false;
    }*/

    @Override
    public Vector getDirection() {
        return null;
    }

    /*@Override
    public boolean hasRotation() {
        return hasRotation;
    }

    @Override
    public float getRotation() {
        return rotation;
    }*/

    @Override
    public boolean isFinished() {
        return false;
    }

    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalWatch(target,getEntity()));
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
