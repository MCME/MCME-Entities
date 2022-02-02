package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.api.*;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface McmeEntity extends Entity, InventoryHolder {

    public UUID getUniqueId();

    public String getName();

    public Location getLocation();

    public void setLocation(Location location);

    public McmeEntityType getMcmeEntityType();

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

    public EntityBoundingBox getEntityBoundingBox();

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

    void setInvisible(boolean visible);

    void setEquipment(EquipmentSlot slot, ItemStack item);

    boolean isOnline();

    public void addPotionEffect(PotionEffect effect);

    public void removePotionEffect(PotionEffect effect);

    public void addItem(ItemStack item, EquipmentSlot slot, int slotId);

    public void removeItem(ItemStack item);

    public Inventory getInventory();

    //default methods for interfaces InventoryHolder and Entity
    @Override
    public default @Nullable Location getLocation(@Nullable Location loc) {
        return null;
    }

    @Override
    public default double getHeight() {
        return 0;
    }

    @Override
    public default double getWidth() {
        return 0;
    }

    @Override
    public default @NotNull BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public default boolean isInWater() {
        return false;
    }

    @Override
    public default @NotNull World getWorld() {
        return null;
    }

    @Override
    public default void setRotation(float yaw, float pitch) {

    }

    @Override
    public default boolean teleport(@NotNull Location location) {
        return false;
    }

    @Override
    public default boolean teleport(@NotNull Location location, PlayerTeleportEvent.@NotNull TeleportCause cause) {
        return false;
    }

    @Override
    public default boolean teleport(@NotNull Entity destination) {
        return false;
    }

    @Override
    public default boolean teleport(@NotNull Entity destination, PlayerTeleportEvent.@NotNull TeleportCause cause) {
        return false;
    }

    @Override
    public default @NotNull List<Entity> getNearbyEntities(double x, double y, double z) {
        return null;
    }

    @Override
    public default int getFireTicks() {
        return 0;
    }

    @Override
    public default int getMaxFireTicks() {
        return 0;
    }

    @Override
    public default void setFireTicks(int ticks) {

    }

    @Override
    public default void remove() {

    }

    @Override
    public default boolean isValid() {
        return false;
    }

    @Override
    public default void sendMessage(@NotNull String message) {

    }

    @Override
    public default void sendMessage(@NotNull String[] messages) {

    }

    @Override
    public default void sendMessage(@Nullable UUID sender, @NotNull String message) {

    }

    @Override
    public default void sendMessage(@Nullable UUID sender, @NotNull String[] messages) {

    }

    @Override
    public default @NotNull Server getServer() {
        return null;
    }

    @Override
    public default boolean isPersistent() {
        return false;
    }

    @Override
    public default void setPersistent(boolean persistent) {

    }

    @Override
    public default @Nullable Entity getPassenger() {
        return null;
    }

    @Override
    public default boolean setPassenger(@NotNull Entity passenger) {
        return false;
    }

    @Override
    public default @NotNull List<Entity> getPassengers() {
        return null;
    }

    @Override
    public default boolean addPassenger(@NotNull Entity passenger) {
        return false;
    }

    @Override
    public default boolean removePassenger(@NotNull Entity passenger) {
        return false;
    }

    @Override
    public default boolean isEmpty() {
        return false;
    }

    @Override
    public default boolean eject() {
        return false;
    }

    @Override
    public default float getFallDistance() {
        return 0;
    }

    @Override
    public default void setFallDistance(float distance) {

    }

    @Override
    public default void setLastDamageCause(@Nullable EntityDamageEvent event) {

    }

    @Override
    public default @Nullable EntityDamageEvent getLastDamageCause() {
        return null;
    }

    @Override
    public default int getTicksLived() {
        return 0;
    }

    @Override
    public default void setTicksLived(int value) {

    }

    @Override
    public default void playEffect(@NotNull EntityEffect type) {

    }

    @Override
    public default @NotNull EntityType getType() {
        return null;
    }

    @Override
    public default boolean isInsideVehicle() {
        return false;
    }

    @Override
    public default boolean leaveVehicle() {
        return false;
    }

    @Override
    public default @Nullable Entity getVehicle() {
        return null;
    }

    @Override
    public default void setCustomNameVisible(boolean flag) {

    }

    @Override
    public default boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public default void setGlowing(boolean flag) {

    }

    @Override
    public default boolean isGlowing() {
        return false;
    }

    @Override
    public default void setInvulnerable(boolean flag) {

    }

    @Override
    public default boolean isInvulnerable() {
        return false;
    }

    @Override
    public default boolean isSilent() {
        return false;
    }

    @Override
    public default void setSilent(boolean flag) {

    }

    @Override
    public default boolean hasGravity() {
        return false;
    }

    @Override
    public default void setGravity(boolean gravity) {

    }

    @Override
    public default int getPortalCooldown() {
        return 0;
    }

    @Override
    public default void setPortalCooldown(int cooldown) {

    }

    @Override
    public default @NotNull Set<String> getScoreboardTags() {
        return null;
    }

    @Override
    public default boolean addScoreboardTag(@NotNull String tag) {
        return false;
    }

    @Override
    public default boolean removeScoreboardTag(@NotNull String tag) {
        return false;
    }

    @Override
    public default @NotNull PistonMoveReaction getPistonMoveReaction() {
        return null;
    }

    @Override
    public default @NotNull BlockFace getFacing() {
        return null;
    }

    @Override
    public default @NotNull Pose getPose() {
        return null;
    }

    @Override
    public default @NotNull Spigot spigot() {
        return null;
    }

    @Override
    public default @Nullable Location getOrigin() {
        return null;
    }

    @Override
    public default boolean fromMobSpawner() {
        return false;
    }

    @Override
    public default @NotNull Chunk getChunk() {
        return null;
    }

    @Override
    public default CreatureSpawnEvent.@NotNull SpawnReason getEntitySpawnReason() {
        return null;
    }

    @Override
    public default boolean isInRain() {
        return false;
    }

    @Override
    public default boolean isInBubbleColumn() {
        return false;
    }

    @Override
    public default boolean isInWaterOrRain() {
        return false;
    }

    @Override
    public default boolean isInWaterOrBubbleColumn() {
        return false;
    }

    @Override
    public default boolean isInWaterOrRainOrBubbleColumn() {
        return false;
    }

    @Override
    public default boolean isInLava() {
        return false;
    }

    @Override
    public default boolean isTicking() {
        return false;
    }

    @Override
    public default @Nullable Component customName() {
        return null;
    }

    @Override
    public default void customName(@Nullable Component customName) {

    }

    @Override
    public default @Nullable String getCustomName() {
        return null;
    }

    @Override
    public default void setCustomName(@Nullable String name) {

    }

    @Override
    public default void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {

    }

    @Override
    public default @NotNull List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        return null;
    }

    @Override
    public default boolean hasMetadata(@NotNull String metadataKey) {
        return false;
    }

    @Override
    public default void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {

    }

    @Override
    public default boolean isPermissionSet(@NotNull String name) {
        return false;
    }

    @Override
    public default boolean isPermissionSet(@NotNull Permission perm) {
        return false;
    }

    @Override
    public default boolean hasPermission(@NotNull String name) {
        return false;
    }

    @Override
    public default boolean hasPermission(@NotNull Permission perm) {
        return false;
    }

    @Override
    public default @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return null;
    }

    @Override
    public default @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return null;
    }

    @Override
    public default @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return null;
    }

    @Override
    public default @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return null;
    }

    @Override
    public default void removeAttachment(@NotNull PermissionAttachment attachment) {

    }

    @Override
    public default void recalculatePermissions() {

    }

    @Override
    public default @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public default boolean isOp() {
        return false;
    }

    @Override
    public default void setOp(boolean value) {

    }

    @Override
    public default @NotNull PersistentDataContainer getPersistentDataContainer() {
        return null;
    }
}
