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

public class GoalMimic extends GoalVirtualEntity {

    private Listener listener;

    private McmeEntity mimic;

    public GoalMimic(VirtualEntity entity, VirtualEntityGoalFactory factory) {
        super(entity, factory);
        this.mimic = factory.getTargetEntity();
    }

    @Override
    public Vector getDirection() {
        return new Vector(1,0,0);
    }

    @Override
    public void update() {
        super.doTick();
        double distance = mimic.getLocation().distanceSquared(getEntity().getLocation());
        if(distance < 0.001) {
            movementSpeed = MovementSpeed.STAND;
        } else if(distance < 0.01) {
            movementSpeed = MovementSpeed.SLOW;
        } else if(distance < 0.1) {
            movementSpeed = MovementSpeed.WALK;
        } else {
            movementSpeed = MovementSpeed.SPRINT;
        }
        getEntity().setLocation(mimic.getLocation());
    }

    public boolean isForceTeleport() {
        return true;
    }

    @Override
    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalMimic(getEntity(), mimic));
    }

    @Override
    public void activate() {
        listener = new MimicListener(getEntity(),mimic);
        Bukkit.getPluginManager().registerEvents(listener, EntitiesPlugin.getInstance());
    }

    @Override
    public void deactivate() {
        HandlerList.unregisterAll(listener);
    }

    @Override
    public VirtualEntityGoalFactory getFactory() {
        return super.getFactory().withTargetEntity(mimic);
    }
}
