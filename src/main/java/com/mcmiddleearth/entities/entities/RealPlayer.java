package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.command.BukkitCommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

public class RealPlayer extends BukkitCommandSender implements McmeEntity {

    public RealPlayer(Player bukkitPlayer) {
        super(bukkitPlayer);
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public Location getLocation() {
        return ((Player)getCommandSender()).getLocation();
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

    public float getRotation() {
        return 0;
    }

    @Override
    public void setRotation(float yaw) {

    }

    @Override
    public EntityBoundingBox getBoundingBox() {
        return null;
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
        return 1;
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
    public boolean onGround() {
        return false;
    }

    @Override
    public int getHealth() {
        return (int)(getBukkitPlayer().getHealth()*20);
    }

    @Override
    public void damage(int damage) {
        getBukkitPlayer().damage(damage);
    }

    @Override
    public void heal(int damage) {
        getBukkitPlayer().setHealth(getBukkitPlayer().getHealth()+damage);
    }

    @Override
    public boolean isDead() {
        return getBukkitPlayer().isDead();
    }

    public Player getBukkitPlayer() {
        return (Player) getCommandSender();
    }

    @Override
    public void sendMessage(BaseComponent[] baseComponents) {
        getBukkitPlayer().sendMessage(baseComponents);
    }

    @Override
    public void playAnimation(AnimationType type) {

    }

    @Override
    public void receiveAttack(McmeEntity damager, int damage, float knockDownFactor) {
        damage(damage);
    }

    @Override
    public void attack(McmeEntity target) {

    }

    @Override
    public Set<McmeEntity> getAttackers() {
        return null;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }
}
