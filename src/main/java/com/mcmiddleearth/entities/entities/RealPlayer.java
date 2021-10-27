package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.goal.GoalDistance;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.api.ActionType;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.command.BukkitCommandSender;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class RealPlayer extends BukkitCommandSender implements McmeEntity {

    private EntityBoundingBox bb = new EntityBoundingBox(0.5,0.3,0,2);

    private static final Random random = new Random();

    private final int updateRandom = random.nextInt(40);

    public RealPlayer(Player bukkitPlayer) {
        super(bukkitPlayer);
        bb.setLocation(bukkitPlayer.getLocation());
        //getBoundingBox();
//Logger.getGlobal().info("Create bb: "+bukkitPlayer.getName());
    }

    public int getUpdateRandom() {
        return updateRandom;
    }

    @Override
    public UUID getUniqueId() {
        return getBukkitPlayer().getUniqueId();
    }

    @Override
    public String getName() {
        return getBukkitPlayer().getName();
    }

    public Location getLocation() {
        return ((Player)getCommandSender()).getLocation();
    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public McmeEntityType getType() {
        return McmeEntityType.valueOf("player");
    }

    @Override
    public Vector getVelocity() {
        return getBukkitPlayer().getVelocity();
    }

    @Override
    public void setVelocity(Vector velocity) {

    }

    @Override
    public float getYaw() {
        return getBukkitPlayer().getLocation().getYaw();
    }

    @Override
    public float getHeadYaw() {
        return getBukkitPlayer().getEyeLocation().getYaw();
    }

    @Override
    public void setRotation(float yaw) {

    }

    @Override
    public EntityBoundingBox getBoundingBox() {
        return bb;
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

    double damage = 0;
    @Override
    public void doTick() {
        bb.setLocation(getBukkitPlayer().getLocation());
//        bb.getBoundingBox();
        /*double attack = 0;
        AttributeInstance attribute = getBukkitPlayer().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if(attribute!=null) attack = attribute.getValue();
        double cooldown = 0;
        attribute = getBukkitPlayer().getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
        if(attribute!=null) cooldown = attribute.getValue();
//ogger.getGlobal().info("Attack: "+attack+" Cooldown: "+cooldown);
        double defense = 0;
        attribute = getBukkitPlayer().getAttribute(Attribute.GENERIC_ARMOR);
        if(attribute!=null) defense = attribute.getValue();
        double toughness = 0;
        attribute = getBukkitPlayer().getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        if(attribute!=null) toughness = attribute.getValue();
        damage++;
        if(damage == 20) damage = 0;
        double dam = damage * (1-Math.min(20,Math.max(defense/5,defense - 4*damage/(toughness+8)))/25);
        Logger.getGlobal().info("defense: "+defense+" toughness: "+toughness+" damage: "+damage+" dam: "+dam);*/
    }

    @Override
    public int getEntityId() {
        return getBukkitPlayer().getEntityId();
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
    public double getHealth() {
        return (int)(getBukkitPlayer().getHealth()*20);
    }

    @Override
    public void damage(double damage) {
        getBukkitPlayer().damage(damage);
    }

    @Override
    public void heal(double damage) {
        getBukkitPlayer().setHealth(getBukkitPlayer().getHealth()+damage);
    }

    @Override
    public boolean isDead() {
        return getBukkitPlayer().isDead();
    }

    public Player getBukkitPlayer() {
        return (Player) getCommandSender();
    }

    /*@Override
    public void sendMessage(BaseComponent[] baseComponents) {
        getBukkitPlayer().sendMessage(baseComponents);
    }*/

    @Override
    public void playAnimation(ActionType type) {

    }

    @Override
    public void receiveAttack(McmeEntity damager, double damage, double knockDownFactor) {
        //knock back?
        double defense = 0;
        AttributeInstance attribute = getBukkitPlayer().getAttribute(Attribute.GENERIC_ARMOR);
        if(attribute!=null) defense = attribute.getValue();
        double toughness = 0;
        attribute = getBukkitPlayer().getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        if(attribute!=null) toughness = attribute.getValue();
        damage(damage*(1-Math.min(20,Math.max(defense/5,defense - 4*damage/(toughness+8)))/25));
        //        damage(damage);
    }

    @Override
    public void attack(McmeEntity target) {
        if(getLocation().distanceSquared(target.getLocation()) < GoalDistance.ATTACK*1.5) {
            //int damage = (int) (Math.random() * 8);
//Logger.getGlobal().info("Attack: " + event.getEntity().getType().getBukkitEntityType() + " " + damage);
            AttributeInstance attribute = getBukkitPlayer().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            double damage = 2;
            if(attribute!= null) damage = attribute.getValue();
            double knockback = 0;
            attribute = getBukkitPlayer().getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
            if(attribute!=null) knockback = attribute.getValue();
            target.receiveAttack(this, damage, knockback+1);
        }

    }

    @Override
    public Set<McmeEntity> getEnemies() {
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
            return MovementType.FALL;
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

    /*@Override
    public ActionType getActionType() {
        return null;
    }*/

    @Override
    public void addPotionEffect(PotionEffect effect) {
        getBukkitPlayer().addPotionEffect(effect);
    }

    @Override
    public void removePotionEffect(PotionEffect effect) {
        getBukkitPlayer().removePotionEffect(effect.getType());
    }

    @Override
    public void addItem(ItemStack item, EquipmentSlot slot, int slotId) {
        ItemStack temp = null;
        PlayerInventory inventory = getBukkitPlayer().getInventory();
        if(slot != null) {
            temp = inventory.getItem(slot);
            inventory.setItem(slot, item);
        } else if(slotId>=0 && slotId < inventory.getSize()){
            temp = inventory.getItem(slotId);
            inventory.setItem(slotId, item);
        } else {
            inventory.addItem(item);
            //TODO drop if inventory full
        }
        if(temp != null) {
            inventory.addItem(temp);
            //TODO drop if inventory full
        }
    }

    @Override
    public void removeItem(ItemStack item) {
        PlayerInventory inventory = getBukkitPlayer().getInventory();
        Arrays.stream(inventory.getContents()).filter(search -> search!=null && search.isSimilar(item))
              .forEach(inventory::removeItemAnySlot);
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
    public float getHeadPitch() {
        return getBukkitPlayer().getEyeLocation().getPitch();
    }

    @Override
    public void setRotation(float yaw, float pitch, float roll) {

    }

    @Override
    public void setInvisible(boolean visible) {
        getBukkitPlayer().setInvisible(visible);
    }

    @Override
    public void setEquipment(EquipmentSlot slot, ItemStack item) {
        getBukkitPlayer().getInventory().setItem(slot, item);
    }
}
