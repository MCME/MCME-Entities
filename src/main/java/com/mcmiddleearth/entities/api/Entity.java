package com.mcmiddleearth.entities.api;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * This interface should not to be implemented directly.
 * If you want to add new entity types you need to implement McmeEntity.
 */
public interface Entity {

    /**
     * Get the name of an entity if it is set.
     * @return Name of the entity
     */
    String getName();

    /**
     * Get the location of an entity. Usually same as bukkit location.
     * @return Location of the entity
     */
    Location getLocation();

    /**
     * Teleport an entity to a new location.
     * @param location Teleportation target
     */
    void setLocation(Location location);

    /**
     * Type of an entity can be any bukkit entity type or a custom type.
     * @return Type of the entity
     */
    McmeEntityType getType();

    /**
     * Velocity of the entity at the last server tick. Unlike bukkit velocity all movements are regarded.
     * Changing the returned object might have unpredictable results.
     * @return Velocity of the entity
     */
    Vector getVelocity();

    /**
     * Externally setting the velocity of an entity may have unpredictable results when the entity has a goal defined.
     * @param velocity Velocity of the entity
     */
    void setVelocity(Vector velocity);

    /**
     * Goals are controlling the behaviour of entities.
     * @return current goal of the entity
     */
    Goal getGoal();

    /**
     *
     * @param goal
     */
    void setGoal(Goal goal);

    public int getEntityId();

    public float getYaw();
    public float getPitch();
    public float getRoll();

    public float getHeadYaw();
    public float getHeadPitch();

    public void setRotation(float yaw);

    public void setRotation(float yaw, float pitch, float roll);

    public EntityBoundingBox getBoundingBox();

    public int getHealth();

    public void damage(int damage);

    public void heal(int damage);

    public boolean isDead();

    public boolean isTerminated();

    public void playAnimation(ActionType type);

    public void receiveAttack(McmeEntity damager, int damage, float knockDownFactor);

    public void attack(McmeEntity target);

    public Set<McmeEntity> getAttackers();

    public Vector getMouth();

    public boolean onGround();

    public MovementType getMovementType();

    public MovementSpeed getMovementSpeed();

    public ActionType getActionType();

}
