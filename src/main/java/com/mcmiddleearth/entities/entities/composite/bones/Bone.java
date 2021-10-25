package com.mcmiddleearth.entities.entities.composite.bones;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.ActionType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import com.mcmiddleearth.entities.protocol.packets.*;
import com.mcmiddleearth.entities.util.RotationMatrix;
import com.mcmiddleearth.entities.util.UuidGenerator;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

public class Bone implements McmeEntity {

    private final String name;

    private final int entityId;

    protected final CompositeEntity parent;

    protected Vector relativePosition, relativePositionRotated, velocity;
    protected EulerAngle headPose, rotatedHeadPose;
    protected float yaw;//, pitch;

    //private float rotation;

    private ItemStack headItem;

    private final UUID uniqueId;

    protected boolean hasItemUpdate, hasHeadPoseUpdate;//, rotationUpdate;
    protected boolean rotationUpdate;

    private final AbstractPacket spawnPacket;
    private final AbstractPacket teleportPacket;
    private final AbstractPacket movePacket;
    private final AbstractPacket metaPacket;
    private final AbstractPacket initPacket;
    private final AbstractPacket namePacket;
    private final SimpleEntityAnimationPacket animationPacket;

    private String displayName;

    private final boolean isHeadBone;

    public Bone(String name, CompositeEntity parent, EulerAngle headPose,
                Vector relativePosition, ItemStack headItem, boolean isHeadBone, int headPoseDelay) {
//long start = System.currentTimeMillis();
        this.name = name;
        this.isHeadBone = isHeadBone;
        uniqueId = UuidGenerator.fast_nullUUID();//UuidGenerator.getRandomV2();
//Logger.getGlobal().info("UUID: "+(System.currentTimeMillis()-start));
        entityId = parent.getEntityId() + parent.getBones().size();
        this.parent = parent;
//Logger.getGlobal().info("Bone get parent parent: "+parent);
        this.relativePosition = relativePosition;
        relativePositionRotated = relativePosition.clone();
//Logger.getGlobal().info("position cloned: "+(System.currentTimeMillis()-start));
        velocity = new Vector(0, 0, 0);
        this.headPose = headPose;
        this.rotatedHeadPose = headPose;
        yaw = 0;
        //pitch = 0;
        //currentYaw = 0;
        //currentPitch = 0;
        this.headItem = headItem;
        spawnPacket = new SimpleNonLivingEntitySpawnPacket(this);
//Logger.getGlobal().info("spawn packet: "+(System.currentTimeMillis()-start));
        teleportPacket = new SimpleEntityTeleportPacket(this);
//Logger.getGlobal().info("teleport packet: "+(System.currentTimeMillis()-start));
        movePacket = new SimpleEntityMovePacket(this);
//Logger.getGlobal().info("move packet: "+(System.currentTimeMillis()-start));
        initPacket = new BoneInitPacket(this);
//Logger.getGlobal().info("init packet: "+(System.currentTimeMillis()-start));
        metaPacket = new BoneMetaPacket(this, headPoseDelay);
//Logger.getGlobal().info("meta packet: "+(System.currentTimeMillis()-start));
        namePacket = new DisplayNamePacket(this.entityId);
//Logger.getGlobal().info("name packet: "+(System.currentTimeMillis()-start));
        animationPacket = new SimpleEntityAnimationPacket(this.entityId);
    }

    @Override
    public void doTick() {
    }

    public void move() {
//Logger.getGlobal().info("move bone to: "+getLocation());
        if (hasHeadPoseUpdate) {
            //currentPitch = turn(currentPitch, pitch);
            rotatedHeadPose = /*RotationMatrix.rotateXEulerAngleDegree(*/headPose/*,pitch)*/;
        }
        Vector shift;
        if (hasRotationUpdate()) {
//Logger.getGlobal().info("Pitch: "+name+" "+relativePosition.toString());
            //Vector pitchCenter = new Vector(0,0,0.3);
            //currentYaw = turn(currentYaw, yaw);
//Logger.getGlobal().info("headpitchcenter: "+parent.getHeadPitchCenter());
            Vector newRelativePositionRotated = RotationMatrix.fastRotateY(/*RotationMatrix
                    .fastRotateX(*/relativePosition/*.clone().subtract(parent.getHeadPitchCenter()),pitch).add(parent.getHeadPitchCenter())*/, -yaw);
            shift = newRelativePositionRotated.clone().subtract(this.relativePositionRotated);
            relativePositionRotated = newRelativePositionRotated;
        } else {
//Logger.getGlobal().info("null vector");
            shift = new Vector(0, 0, 0);
        }


        velocity = parent.getVelocity().clone().add(shift);

    }

    public void teleport() {
//Logger.getGlobal().info("Teleport bone!");
        if (hasHeadPoseUpdate) {
            //currentYaw = yaw;
            //currentPitch = pitch;
            rotatedHeadPose = /*RotationMatrix.rotateXEulerAngleDegree(*/headPose/*, pitch)*/;
        }
        relativePositionRotated = RotationMatrix.fastRotateY(/*RotationMatrix
                    .fastRotateX(*/relativePosition/*.clone().subtract(parent.getHeadPitchCenter()),pitch).add(parent.getHeadPitchCenter())*/, -yaw);
    }

    public void resetUpdateFlags() {
        /*if(currentYaw == yaw) {
            if(currentPitch == pitch) {
                hasHeadPitchUpdate = false;
            }
            rotationUpdate = false;
        }*/
        hasHeadPoseUpdate = false;
        rotationUpdate = false;
        hasItemUpdate = false;
        //rotationUpdate = false;
    }

    public ItemStack getHeadItem() {
        return headItem;
    }

    public void setHeadItem(ItemStack headItem) {
        if (!headItem.equals(this.headItem)) {
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

    public AbstractPacket getMetaPacket() {
        return metaPacket;
    }

    public AbstractPacket getInitPacket() {
        return initPacket;
    }

    public SimpleEntityAnimationPacket getAnimationPacket() {
        return animationPacket;
    }

    public boolean isHeadBone() {
        return isHeadBone;
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
    public void setLocation(Location location) {
    }

    @Override
    public McmeEntityType getType() {
        return new McmeEntityType(McmeEntityType.CustomEntityType.BONE);
    }

    @Override
    public Vector getVelocity() {
        return velocity;
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
    public int getEntityId() {
        return entityId;
    }

    @Override
    public boolean hasId(int entityId) {
        return this.entityId == entityId;
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
        return rotationUpdate || parent.hasRotationUpdate();
    }

    /*@Override
    public boolean onGround() {
        return false;
    }**/

    @Override
    public float getYaw() {
        return /*parent.getRotation()+ */ yaw;
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
    public void setRotation(float yaw, float pitch, float roll) {

    }

    @Override
    public void setRotation(float yaw) {
        this.yaw = yaw;//-parent.getRotation();
        rotationUpdate = true;
    }

    /*public void setPitch(float pitch) {
        this.pitch = pitch;
        rotationUpdate = true;
        hasHeadPoseUpdate = true;
    }*/

    @Override
    public EntityBoundingBox getBoundingBox() {
        return null;
    }

    public Vector getRelativePosition() {
        return relativePosition;
    }

    public void setRelativePosition(Vector relativePosition) {
        this.relativePosition = relativePosition;
        rotationUpdate = true;
    }

    public void setHeadPose(EulerAngle headPose) {
        if (!headPose.equals(this.headPose)) {
            hasHeadPoseUpdate = true;
            this.headPose = headPose;
        }
    }

    public EulerAngle getRotatedHeadPose() {
        return rotatedHeadPose;
    }

    public boolean isHasHeadPoseUpdate() {
        return hasHeadPoseUpdate;
    }

    public boolean isHasItemUpdate() {
        return hasItemUpdate;
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
    public boolean isTerminated() {
        return parent.isTerminated();
    }

    @Override
    public void playAnimation(ActionType type) {

    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        ((DisplayNamePacket) namePacket).setName(displayName);
        namePacket.send(parent.getViewers());
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void finalise() {
    }

    @Override
    public Vector getMouth() {
        return new Vector(0, 0, 0);
    }

    @Override
    public MovementType getMovementType() {
        return MovementType.FLYING;
    }

    @Override
    public boolean onGround() {
        return false;
    }

    @Override
    public MovementSpeed getMovementSpeed() {
        return MovementSpeed.STAND;
    }

    /*@Override
    public ActionType getActionType() {
        return ActionType.IDLE;
    }*/

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

    @Override
    public void setInvisible(boolean visible) {
        //TODO
    }

    @Override
    public void setEquipment(EquipmentSlot slot, ItemStack item) {
        //do nothing
    }

    @Override
    public void removeItem(ItemStack item) {
        //do nothing
    }
}