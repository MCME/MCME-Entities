package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.api.*;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

public interface McmeEntity extends Entity {

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

    //public boolean onGround();

    public float getYaw();
    public float getPitch();
    public float getRoll();

    public float getHeadYaw();
    public float getHeadPitch();

    public void setRotation(float yaw);

    public void setRotation(float yaw, float pitch, float roll);

    public EntityBoundingBox getBoundingBox();

    public double getHealth();
    public void damage(double damage);
    public void heal(double damage);
    public boolean isDead();

    public boolean isTerminated();

    public void playAnimation(ActionType type);

    public void receiveAttack(McmeEntity damager, double damage, double knockDownFactor);
    public void attack(McmeEntity target);

    public Set<McmeEntity> getEnemies();

    public void finalise();

    public Vector getMouth();

    public boolean onGround();

    public MovementType getMovementType();

    public MovementSpeed getMovementSpeed();

    //public ActionType getActionType();

    public boolean hasId(int entityId);
}
