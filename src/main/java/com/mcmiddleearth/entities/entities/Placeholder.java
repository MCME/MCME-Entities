package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.api.ActionType;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.MovementType;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

public class Placeholder implements McmeEntity {

    private final UUID uniqueId;

    public Placeholder(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public McmeEntityType getType() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }

    @Override
    public void setVelocity(Vector velocity) {

    }

    @Override
    public Location getTarget() {
        return null;
    }

    @Override
    public Goal getGoal() {
        return null;
    }

    @Override
    public void setGoal(Goal goal) {

    }

    @Override
    public void doTick() {

    }

    @Override
    public int getEntityId() {
        return 0;
    }

    @Override
    public int getEntityQuantity() {
        return 0;
    }

    @Override
    public boolean hasLookUpdate() {
        return false;
    }

    @Override
    public boolean hasRotationUpdate() {
        return false;
    }

    @Override
    public float getYaw() {
        return 0;
    }

    @Override
    public float getPitch() {
        return 0;
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public float getHeadYaw() {
        return 0;
    }

    @Override
    public float getHeadPitch() {
        return 0;
    }

    @Override
    public void setRotation(float yaw) {

    }

    @Override
    public void setRotation(float yaw, float pitch, float roll) {

    }

    @Override
    public EntityBoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public void damage(double damage) {

    }

    @Override
    public void heal(double damage) {

    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public void playAnimation(ActionType type) {

    }

    @Override
    public void receiveAttack(McmeEntity damager, double damage, double knockDownFactor) {

    }

    @Override
    public void attack(McmeEntity target) {

    }

    @Override
    public Set<McmeEntity> getEnemies() {
        return null;
    }

    @Override
    public void finalise() {

    }

    @Override
    public Vector getMouth() {
        return null;
    }

    @Override
    public boolean onGround() {
        return false;
    }

    @Override
    public MovementType getMovementType() {
        return null;
    }

    @Override
    public MovementSpeed getMovementSpeed() {
        return null;
    }

    /*@Override
    public ActionType getActionType() {
        return null;
    }*/

    @Override
    public boolean hasId(int entityId) {
        return false;
    }

    @Override
    public void addPotionEffect(PotionEffect effect) {
        //do nothing
    }

    @Override
    public void removePotionEffect(PotionEffect effect) {
        //do nothing
    }

    @Override
    public void addItem(ItemStack item, EquipmentSlot slot, int slotId) {
        //do nothing
    }
}
