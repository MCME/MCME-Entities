package com.mcmiddleearth.entities.entities;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.goal.GoalDistance;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.api.ActionType;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.command.BukkitCommandSender;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class RealPlayer extends BukkitCommandSender implements McmeEntity {

    private EntityBoundingBox bb = new EntityBoundingBox(0.5,0.3,0,2);

    private static final Random random = new Random();

    private final int updateRandom = random.nextInt(40);

    private final Set<String> tags = new HashSet<>();

    private final VirtualEntityAttributeInstance knockBackAttribute
            = new VirtualEntityAttributeInstance(Attribute.GENERIC_ATTACK_KNOCKBACK,0.4,0.4);

    private static final File playerFolder = new File(EntitiesPlugin.getInstance().getDataFolder(),"realplayer");

    public RealPlayer(Player bukkitPlayer) {
        super(bukkitPlayer);
        bb.setLocation(bukkitPlayer.getLocation());
        load();
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
    public McmeEntityType getMcmeEntityType() {
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
    public EntityBoundingBox getEntityBoundingBox() {
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
//Logger.getGlobal().info("Player damage: "+damage);
        getBukkitPlayer().damage(damage);
    }

    @Override
    public void heal(double damage) {
        getBukkitPlayer().setHealth(getBukkitPlayer().getHealth()+damage);
    }

    @Override
    public boolean isDead() {
        if(!getBukkitPlayer().isOnline()) return false;
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
//Logger.getGlobal().info("Damage dealt: "+damage+" toughness: "+toughness+" defense: "+defense);
        double playerDamage = damage*(1-Math.min(20,Math.max(defense/5,defense - 4*damage/(toughness+8)))/25);
//Logger.getGlobal().info("playerDamage: "+damage);
        double armor=0;
        for(ItemStack item : getBukkitPlayer().getInventory().getArmorContents()) {
//Logger.getGlobal().info("item: "+item);
            if(item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                    armor += meta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
//Logger.getGlobal().info("armor: "+armor);
                }
            }
        }
        armor = Math.min(20,armor)/25;
//Logger.getGlobal().info("final armor: "+armor);
        playerDamage = (1-armor) * playerDamage;
        damage(playerDamage);
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
            ItemStack weapon = getBukkitPlayer().getInventory().getItemInMainHand();
            ItemMeta meta = weapon.getItemMeta();
            if(meta !=null) {
                try {
                    knockBackAttribute.setModifiers(meta.getAttributeModifiers(Attribute.GENERIC_ATTACK_KNOCKBACK));
                    knockback = knockBackAttribute.getValue();
                } catch (NullPointerException ignore) {
                }
                if(meta.hasEnchant(Enchantment.KNOCKBACK)) {
                    knockback += meta.getEnchantLevel(Enchantment.KNOCKBACK)*0.1;
                }
                if(meta.hasEnchant(Enchantment.DAMAGE_ALL)) {
                    damage += (0.5 + meta.getEnchantLevel(Enchantment.DAMAGE_ALL)*0.5);
                }
            }
           //if(attribute!=null) knockback = attribute.getValue();
            target.receiveAttack(this, damage, knockback+1);
//Logger.getGlobal().info("damage: "+damage+" knockback: "+knockback);
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
            drop(inventory.addItem(item));
        }
        if(temp != null) {
            drop(inventory.addItem(temp));
        }
    }

    private void drop(Map<Integer,ItemStack> drops) {
        Location loc = getBukkitPlayer().getLocation().clone();
        loc.setPitch(0);
        loc.add(loc.getDirection().multiply(2));
        Block block = loc.getBlock();
        while(!block.isPassable() && block.getY()<loc.getWorld().getMaxHeight()) {
            block = block.getRelative(BlockFace.UP);
        }
        Location location = block.getLocation();
        drops.values().forEach(drop -> location.getWorld().dropItemNaturally(location,drop));
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

    @Override
    public boolean isOnGround() {
        return getBukkitPlayer().isOnGround();
    }

    @Override
    public boolean isOnline() {
        return getBukkitPlayer().isOnline();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getBukkitPlayer().getInventory();
    };

    @Override
    public Set<String> getTagList() {
        return tags;
    }

    @Override
    public void addTag(String tag) {
        McmeEntity.super.addTag(tag);
        save();
    }

    @Override
    public void removeTag(String tag) {
        McmeEntity.super.removeTag(tag);
        save();
    }

    private void load() {
        File file = new File(playerFolder,getBukkitPlayer().getUniqueId()+".json");
        if(file.exists()) {
            Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
            try (JsonReader reader = gson.newJsonReader(new FileReader(file))) {
                reader.beginObject();
                while(reader.hasNext()) {
                    String key = reader.nextName();
                    switch(key) {
                        case "tags":
                            reader.beginArray();
                            while(reader.hasNext()) {
                                tags.add(reader.nextString());
                            }
                            reader.endArray();
                    }
                }
                reader.endObject();
            } catch (IOException e) {
                Logger.getLogger(RealPlayer.class.getName()).warning("File input error.");
            }
        }
    }

    private void save() {
        File file = new File(playerFolder,getBukkitPlayer().getUniqueId()+".json");
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        try (JsonWriter writer = gson.newJsonWriter(new FileWriter(file))) {
            writer.beginObject()
                  .name("tags")
                  .beginArray();
            for(String tag: tags) {
                writer.value(tag);
            }
            writer.endArray()
                  .endObject();
        } catch (IOException e) {
            Logger.getLogger(RealPlayer.class.getName()).warning("File output error.");
        }
    }
}
