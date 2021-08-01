package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.entities.AnimationType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.McmeEntityType;
import com.mcmiddleearth.entities.protocol.packets.*;
import com.mcmiddleearth.entities.util.RotationMatrix;
import com.mcmiddleearth.entities.util.UuidGenerator;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class Bone implements McmeEntity {

    private final String name;

    private final int entityId;

    protected final CompositeEntity parent;

    protected Vector relativePosition, relativePositionRotated, velocity;
    private EulerAngle headPose;

    //private float rotation;

    private ItemStack headItem;

    private final UUID uniqueId;

    private boolean hasItemUpdate, hasHeadRotationUpdate;//, rotationUpdate;

    private final AbstractPacket spawnPacket;
    private final AbstractPacket teleportPacket;
    private final AbstractPacket movePacket;
    private final AbstractPacket metaPacket;
    private final AbstractPacket initPacket;
    private final AbstractPacket namePacket;

    private String displayName;

    public Bone(String name, CompositeEntity parent, EulerAngle headPose,
                Vector relativePosition, ItemStack headItem) {
//long start = System.currentTimeMillis();
        this.name = name;
        uniqueId = UuidGenerator.fast_nullUUID();//UuidGenerator.getRandomV2();
//Logger.getGlobal().info("UUID: "+(System.currentTimeMillis()-start));
        entityId = parent.getEntityId()+parent.getBones().size();
        this.parent = parent;
//Logger.getGlobal().info("Bone get parent parent: "+parent);
        this.relativePosition = relativePosition;
        relativePositionRotated = relativePosition.clone();
//Logger.getGlobal().info("position cloned: "+(System.currentTimeMillis()-start));
        velocity = new Vector(0,0,0);
        this.headPose = headPose;
        this.headItem = headItem;
        spawnPacket = new SimpleNonLivingEntitySpawnPacket(this);
//Logger.getGlobal().info("spawn packet: "+(System.currentTimeMillis()-start));
        teleportPacket = new SimpleEntityTeleportPacket(this);
//Logger.getGlobal().info("teleport packet: "+(System.currentTimeMillis()-start));
        movePacket = new SimpleEntityMovePacket(this);
//Logger.getGlobal().info("move packet: "+(System.currentTimeMillis()-start));
        initPacket = new BoneInitPacket(this);
//Logger.getGlobal().info("init packet: "+(System.currentTimeMillis()-start));
        metaPacket = new BoneMetaPacket(this);
//Logger.getGlobal().info("meta packet: "+(System.currentTimeMillis()-start));
        namePacket = new DisplayNamePacket(this.entityId);
//Logger.getGlobal().info("name packet: "+(System.currentTimeMillis()-start));
    }

    @Override
    public void doTick() {}

    public void move() {
//Logger.getGlobal().info("move bone to: "+getLocation());
        Vector newRelativePositionRotated = RotationMatrix.fastRotateY(relativePosition, -parent.getRotation());
        Vector shift = newRelativePositionRotated.clone().subtract(this.relativePositionRotated);

        velocity = parent.getVelocity().clone().add(shift);

        relativePositionRotated = newRelativePositionRotated;
    }

    public void teleport() {
        relativePositionRotated = RotationMatrix.fastRotateY(relativePosition, -parent.getRotation());
    }

    public void resetUpdateFlags() {
        hasHeadRotationUpdate = false;
        hasItemUpdate = false;
        //rotationUpdate = false;
    }

    public ItemStack getHeadItem() {
        return headItem;
    }

    public void setHeadItem(ItemStack headItem) {
        if(!headItem.equals(this.headItem)) {
            hasItemUpdate = true;
            this.headItem = headItem;
        }
    }

    public AbstractPacket getSpawnPacket() {
        return spawnPacket;
    }

    public AbstractPacket getNamePacket() {
        return namePacket;
    }

    public AbstractPacket getTeleportPacket() {
        return teleportPacket;
    }

    public AbstractPacket getMovePacket() {
        return movePacket;
    }

    public AbstractPacket getMetaPacket() { return metaPacket; }

    public AbstractPacket getInitPacket() {
        return initPacket;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
//Logger.getGlobal().info("Bone get location  parent: "+parent);
//Logger.getGlobal().info("Bone get location  location: "+parent.getLocation());
        return parent.getLocation().clone().add(relativePositionRotated);
    }

    @Override
    public void setLocation(Location location) {}

    @Override
    public McmeEntityType getType() {
        return new McmeEntityType(McmeEntityType.CustomEntityType.BONE);
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vector velocity) {}

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
    public int getEntityId() {
        return entityId;
    }

    @Override
    public int getEntityQuantity() {
        return 1;
    }

    @Override
    public boolean hasLookUpdate() {
        return true;
    }

    @Override
    public boolean hasRotationUpdate() {
        return parent.hasRotationUpdate();
    }

    @Override
    public boolean onGround() {
        return false;
    }

    @Override
    public float getRotation() {
        return parent.getRotation();
    }

    @Override
    public  void setRotation(float yaw) {
        //rotation = yaw;
        //rotationUpdate = true;
    }

    @Override
    public EntityBoundingBox getBoundingBox() {
        return null;
    }

    public Vector getRelativePosition() {
        return relativePosition;
    }

    public EulerAngle getHeadPose() {
        return headPose;
    }

    public void setRelativePosition(Vector relativePosition) {
        this.relativePosition = relativePosition;
    }

    public void setHeadPose(EulerAngle headPose) {
        if(!headPose.equals(this.headPose)) {
            hasHeadRotationUpdate = true;
            this.headPose = headPose;
        }
    }

    public boolean isHasItemUpdate() {
        return hasItemUpdate;
    }

    public boolean isHasHeadRotationUpdate() {
        return hasHeadRotationUpdate;
    }

    @Override
    public int getHealth() {
        return 0;
    }

    @Override
    public void damage(int damage) {

    }

    @Override
    public void heal(int damage) {

    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public void playAnimation(AnimationType type) {

    }

    @Override
    public void receiveAttack(McmeEntity damager, int damage, float knockDownFactor) {

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
        return parent.isTerminated();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        ((DisplayNamePacket)namePacket).setName(displayName);
        namePacket.send(parent.getViewers());
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void finalise() {}

    @Override
    public Vector getMouth() {
        return new Vector(0,0,0);
    }
}
