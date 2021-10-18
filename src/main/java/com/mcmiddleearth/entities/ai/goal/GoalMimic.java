package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalMimic;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.listener.MimicListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class GoalMimic extends GoalVirtualEntity {

    private Listener listener;

    private McmeEntity mimic;

    private Vector velocity;

    private float bodyYaw;

    public GoalMimic(VirtualEntity entity, VirtualEntityGoalFactory factory) {
        super(entity, factory);
        this.mimic = factory.getTargetEntity();
        setDefaultHeadGoal();
        bodyYaw = mimic.getYaw();
    }

    @Override
    public Vector getDirection() {
        return new Vector(1,0,0);
    }

    public McmeEntity getMimic() {
        return mimic;
    }

    @Override
    public void doTick() {
        super.doTick();

        double distance = mimic.getLocation().distanceSquared(getEntity().getLocation());
        if(distance < 0.0001) {
            movementSpeed = MovementSpeed.STAND;
        } else if(distance < 0.001) {
            movementSpeed = MovementSpeed.SLOW;
        } else if(distance < 0.1) {
            movementSpeed = MovementSpeed.WALK;
        } else {
            movementSpeed = MovementSpeed.SPRINT;
        }

        if(distance > 0.0001) {
            Vector move = mimic.getLocation().subtract(getEntity().getLocation()).toVector();
            float moveYaw = mimic.getLocation().setDirection(move).getYaw();
            float bukkitYaw = mimic.getLocation().getYaw();
            float yawDiff = moveYaw-bukkitYaw;
            while(yawDiff > 180) yawDiff -= 360;
            while(yawDiff <= -180) yawDiff += 360;

//Logger.getGlobal().info("bukkit: "+mimic.getLocation().getYaw()+" move: "+moveYaw+" Diff: "+yawDiff);
            if(yawDiff > 40 && yawDiff < 100 || yawDiff < -100 && yawDiff > -140 ) {
                bodyYaw = mimic.getLocation().getYaw() + 45;
            } else if(yawDiff < -40 && yawDiff > -140 || yawDiff > 100 && yawDiff < 140) {
                bodyYaw = mimic.getLocation().getYaw() - 45;
            } else {
                bodyYaw = mimic.getLocation().getYaw();
            }
            if(yawDiff > 100 || yawDiff < -100) movementSpeed = MovementSpeed.BACKWARD;
        } else {
            float bukkitYaw = mimic.getLocation().getYaw();
            while(bukkitYaw < -180) bukkitYaw += 360;
            while(bukkitYaw > 180) bukkitYaw -= 360;
            while(bodyYaw < -180) bodyYaw += 360;
            while(bodyYaw > 180) bodyYaw -= 360;
            if(bukkitYaw > 90 && bodyYaw < -90) bodyYaw += 360;
            if(bukkitYaw < -90 && bodyYaw > 90) bodyYaw -= 360;
//Logger.getGlobal().info("bukkit: "+bukkitYaw+" yaw: "+bodyYaw);
            if(bukkitYaw > bodyYaw +30) {
//Logger.getGlobal().info("minus 30");
                bodyYaw = bukkitYaw-30;
            } else if(bukkitYaw < bodyYaw -30){
//Logger.getGlobal().info("plus 30");
                bodyYaw = bukkitYaw+30;
            }
            while(bodyYaw > 180) bodyYaw -= 360;
            while(bodyYaw <= -180) bodyYaw += 360;
        }
        //getEntity().setLocation(mimic.getLocation());
        setYaw(bodyYaw);
        velocity = mimic.getLocation().clone().subtract(getEntity().getLocation()).toVector();
    }

    public boolean isDirectMovementControl() {
        return true;
    }

    @Override
    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalMimic(getEntity(), mimic,10));
    }

    @Override
    public void activate() {
        listener = new MimicListener(getEntity(),mimic);
        Bukkit.getPluginManager().registerEvents(listener, EntitiesPlugin.getInstance());
        mimic.setInvisible(true);
    }

    @Override
    public void deactivate() {
        HandlerList.unregisterAll(listener);
        mimic.setInvisible(false);
    }

    @Override
    public VirtualEntityGoalFactory getFactory() {
        return super.getFactory().withTargetEntity(mimic);
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }


}
