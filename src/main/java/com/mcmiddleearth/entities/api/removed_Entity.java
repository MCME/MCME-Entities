package com.mcmiddleearth.entities.api;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

/**
 * This interface should not to be implemented directly.
 * If you want to add new entity types you need to implement McmeEntity.
 */
public interface removed_Entity {

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

    public UUID getUniqueId();

    public int getEntityId();

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

    public Vector getMouth();

    public boolean onGround();

    public MovementType getMovementType();

    public MovementSpeed getMovementSpeed();

    //public ActionType getActionType();

    public void addPotionEffect(PotionEffect effect);

    public void removePotionEffect(PotionEffect effect);

    public void addItem(ItemStack item, EquipmentSlot slot, int slotId);

    public void removeItem(ItemStack item);

    public Inventory getInventory();
}
