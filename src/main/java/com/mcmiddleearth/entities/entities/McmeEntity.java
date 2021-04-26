package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goals.Goal;
import com.mcmiddleearth.entities.ai.goals.VirtualEntityGoal;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.UUID;

public interface McmeEntity {

    public UUID getUniqueId();

    public String getName();

    public Location getLocation();

    public void setLocation(Location location);

    public McmeEntityType getType();

    public Vector getVelocity();

    public void setVelocity(Vector velocity);

    public Location getTarget();

    public Goal getGoal();

    public void setGoal(Goal goal);

    public void doTick();

    public int getEntityId();

    public int getEntityQuantity();

    public boolean hasLookUpdate();

    public boolean hasRotationUpdate();

    public boolean onGround();

    public float getRotation();

    public void setRotation(float yaw);

    public EntityBoundingBox getBoundingBox();
}
