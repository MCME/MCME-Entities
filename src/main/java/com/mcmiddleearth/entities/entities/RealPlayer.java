package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.goal.GoalDistance;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.ai.movement.MovementSpeed;
import com.mcmiddleearth.entities.ai.movement.MovementType;
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

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public float getHeadYaw() {
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
    public boolean hasId(int entityId) {
        return false;
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

    /*@Override
    public boolean onGround() {
        return false;
    }*/

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
    public void playAnimation(ActionType type) {

    }

    @Override
    public void receiveAttack(McmeEntity damager, int damage, float knockDownFactor) {
        //knock back?
        damage(damage);
    }

    @Override
    public void attack(McmeEntity target) {
        if(getLocation().distanceSquared(target.getLocation()) < GoalDistance.ATTACK*1.5) {
            int damage = (int) (Math.random() * 8);
//Logger.getGlobal().info("Attack: " + event.getEntity().getType().getBukkitEntityType() + " " + damage);
            target.receiveAttack(this, damage, 1);
        }

    }

    @Override
    public Set<McmeEntity> getAttackers() {
        return null;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public void finalise() {}

    @Override
    public Vector getMouth() {
        return new Vector(0,1.8,0);
    }

    @Override
    public MovementType getMovementType() {
        if(getBukkitPlayer().isFlying()) {
            return MovementType.FLYING;
        } else if(getBukkitPlayer().isGliding()) {
            return MovementType.GLIDING;
        } else if(getBukkitPlayer().isSwimming()) {
            return MovementType.SWIMMING;
        } else if(getBukkitPlayer().isSneaking()) {
            return MovementType.SNEAKING;
        } else if(getBukkitPlayer().isOnGround()) {
            return MovementType.UPRIGHT;
        } else {
            return MovementType.FALLING;
        }
    }

    @Override
    public MovementSpeed getMovementSpeed() {
        if(getBukkitPlayer().isSprinting()) {
            return MovementSpeed.SPRINT;
        } else if(getBukkitPlayer().isSneaking()) {
            return MovementSpeed.SLOW;
        } else {
            return MovementSpeed.WALK;
        }
    }

    @Override
    public boolean onGround() {
        return getBukkitPlayer().isOnGround();
    }

    @Override
    public ActionType getActionType() {
        return null;
    }
}
