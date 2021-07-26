package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Set;
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

    public int getHealth();
    public void damage(int damage);
    public void heal(int damage);
    public boolean isDead();

    public boolean isTerminated();

    public void playAnimation(AnimationType type);

    public void receiveAttack(McmeEntity damager, int damage, float knockDownFactor);
    public void attack(McmeEntity target);

    public Set<McmeEntity> getAttackers();
}
