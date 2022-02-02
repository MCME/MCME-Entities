package com.mcmiddleearth.entities.api;

import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Projectile;
import com.mcmiddleearth.entities.entities.attributes.VirtualAttributeFactory;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import com.mcmiddleearth.entities.entities.simple.SimpleHorse;
import com.mcmiddleearth.entities.entities.simple.SimpleLivingEntity;
import com.mcmiddleearth.entities.entities.simple.SimpleNonLivingEntity;
import com.mcmiddleearth.entities.entities.simple.SimplePlayer;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * VirtualEntityFactory class is used to provide all required information during spawn process of an entity.
 */
public class VirtualEntityFactory {

    private McmeEntityType type;

    private Set<UUID> whitelist = null;
    private boolean useWhitelistAsBlacklist = false;

    private UUID uniqueId = null;

    private String name = "", dataFile = "", displayName = null;

    private Vector displayNamePosition = new Vector(0,0,0);

    private Location location;

    private float roll = 0;

    private float headYaw = 0, headPitch = 0;

    private McmeEntity spawnLocationEntity = null;

    private double health = -1;

    private MovementType movementType = MovementType.UPRIGHT;

    private Map<Attribute, AttributeInstance> attributes;

    private EntityBoundingBox boundingBox = EntityBoundingBox.getBoundingBox(new McmeEntityType(EntityType.SKELETON));

    private VirtualEntityGoalFactory goalFactory = null;

    private Vector headPitchCenter = new Vector(0,0.1,-0.03);

    private SpeechBalloonLayout speechBalloonLayout = new SpeechBalloonLayout();

    private Vector mouth = new Vector(0,1.7,0);

    private boolean manualAnimationControl = false;

    private int headPoseDelay = 2;

    private int viewDistance = 320;

    private float maxRotationStep = 40f;
    private float maxRotationStepFlight = 2f;

    private int updateInterval = 10;

    private int jumpHeight = 1;
    private float knockBackBase = 0.1f, knockBackPerDamage = 0.005f;
    private float projectileDamage = 2;
    private int attackDelay = 0;

    private McmeEntity shooter = null;
    private float projectileVelocity = 1.6f;

    private Set<McmeEntity> enemies = null;

    private Vector attackPoint = new Vector(0,1,0);

    private Vector saddlePoint = new Vector(0,1.4,0);

    private Vector sitPoint = new Vector (0,1,0);

    private boolean writeDefaultValuesToFile = false;

    private boolean saddled = false;
    private Horse.Color horseColor = Horse.Color.WHITE;
    private Horse.Style horseStyle = Horse.Style.NONE;

    private org.bukkit.entity.Entity dependingEntity;

    public VirtualEntityFactory(McmeEntityType type, Location location) {
        //uniqueId = UuidGenerator.fast_random();//getRandomV2();
        this.type = type;
        this.location = location;
        attributes = VirtualAttributeFactory.getAttributesFor(type);
        boundingBox = EntityBoundingBox.getBoundingBox(type);
    }

    public VirtualEntityFactory(McmeEntityType type, Location location, boolean invertWhitelist,
                                 UUID uniqueId, String name, Map<Attribute, AttributeInstance> attributes) {
        this.type = type;
        this.location = location;
        this.useWhitelistAsBlacklist = invertWhitelist;
        this.uniqueId = uniqueId;
        this.name = name;
        this.attributes = attributes;
    }

    public static Collection<String> availableProperties() {
        return Stream.of("type","blacklist","uniqueId", "name", "dataFile", "displayName","displayNamePosition",
                "location","movementType","goalType","targetLocation","targetEntity","headPitchCenter",
                "speechballoonlayout","mouth","manualanimation","headposedelay","viewdistance",
                "maxrotationstep", "maxRotationStepFlight", "updateInterval", "jumpheight", "knockbackbase",
                "knockbackperdamage","relative_position","saddlepoint","sitpoint","attackpoint",
                "flightlevel","dive","attackpitch","saddle","color","style","attackdelay").map(String::toLowerCase)
                .sorted().collect(Collectors.toList());
    }

    public VirtualEntityFactory withUuid(UUID uuid) {
        this.uniqueId = uuid;
        return this;
    }

    public VirtualEntityFactory withName(String name) {
        this.name = name;
        return this;
    }

    public VirtualEntityFactory withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public VirtualEntityFactory withDisplayNamePosition(Vector position) {
        this.displayNamePosition = position;
        return this;
    }

    public VirtualEntityFactory withBlackList(boolean useBlacklist) {
        this.useWhitelistAsBlacklist = useBlacklist;
        return this;
    }

    public boolean hasBlackList() {
        return useWhitelistAsBlacklist;
    }

    public VirtualEntityFactory withAttribute(Attribute attribute, double baseValue) {
        attributes.put(attribute, VirtualAttributeFactory.getAttributeInstance(attribute,baseValue));
        return this;
    }

    public VirtualEntityFactory withAttributes(Map<Attribute,AttributeInstance> attributes) {
        this.attributes = attributes;
        return this;
    }

    public VirtualEntityFactory withMovementType(MovementType movementType) {
        this.movementType = movementType;
        return this;
    }

    public VirtualEntityFactory withBoundingBox(EntityBoundingBox boundingBox) {
        this.boundingBox = new EntityBoundingBox(boundingBox);
        return this;
    }

    public float getRoll() {
        return roll;
    }

    public VirtualEntityFactory withRoll(float roll) {
        this.roll = roll;
        return this;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public VirtualEntityFactory withHeadYaw(float headYaw) {
        this.headYaw = headYaw;
        return this;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public VirtualEntityFactory withHeadPitch(float headPitch) {
        this.headPitch = headPitch;
        return this;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public VirtualEntityFactory withUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    public VirtualEntityFactory withDataFile(String filename) {
        this.dataFile = filename;
        return this;
    }

    public String getDataFile() {
        return this.dataFile;
    }

    public VirtualEntityFactory withLocation(Location location) {
        this.location = location;
        spawnLocationEntity = null;
        return this;
    }

    public VirtualEntityFactory withEntityForSpawnLocation(McmeEntity entity) {
        this.spawnLocationEntity = entity;
        return this;
    }

    public McmeEntity getSpawnLocationEntity() {
        return spawnLocationEntity;
    }

    public VirtualEntityFactory withSpeechBalloonLayout(SpeechBalloonLayout layout) {
        this.speechBalloonLayout = layout;
        return this;
    }

    public SpeechBalloonLayout getSpeechBalloonLayout() { return speechBalloonLayout; }

    public VirtualEntityFactory withMouth(Vector mouth) {
        this.mouth = mouth;
        return this;
    }

    public VirtualEntityFactory withHeadPitchCenter(Vector pitchCenter) {
        this.headPitchCenter = pitchCenter;
        return this;
    }

    public Vector getHeadPitchCenter() {
        return headPitchCenter;
    }

    public VirtualEntityFactory withManualAnimationControl(boolean manualControl) {
        manualAnimationControl = manualControl;
        return this;
    }

    public boolean getManualAnimationControl() {
        return manualAnimationControl;
    }

    public VirtualEntityFactory withHeadPoseDelay(int headPoseDelay) {
        this.headPoseDelay = headPoseDelay;
        return this;
    }

    public int getHeadPoseDelay() {
        return headPoseDelay;
    }

    public VirtualEntityFactory withGoalFactory(VirtualEntityGoalFactory factory) {
        this.goalFactory = factory;
        return  this;
    }

    public VirtualEntityGoalFactory getGoalFactory() {
        return goalFactory;
    }

    public EntityBoundingBox getBoundingBox() {
        return new EntityBoundingBox(boundingBox);
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public VirtualEntityFactory withEntityType(McmeEntityType type) {
        this.type = type;
        return this;
    }
    public McmeEntityType getType() {
        return type;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Vector getDisplayNamePosition() { return displayNamePosition; }

    public Location getLocation() {
        if(spawnLocationEntity!=null) {
            return spawnLocationEntity.getLocation().clone();
        }
        return (location!=null?location.clone():null);
    }

    public Vector getMouth() { return mouth; }

    /**
     * Attributes are not yet implemented.
     * @return
     */
    public Map<Attribute, AttributeInstance> getAttributes() {
        Map<Attribute,AttributeInstance> result = new HashMap<>();
        if(attributes!=null) {
            attributes.forEach((attribute, instance)
                    -> result.put(attribute, new VirtualEntityAttributeInstance(attribute,
                    instance.getBaseValue(),
                    instance.getDefaultValue())));
        }
        return result;
    }

    public int getJumpHeight() {
        return jumpHeight;
    }

    public VirtualEntityFactory withJumpHeight(int jumpHeight) {
        this.jumpHeight = jumpHeight;
        return this;
    }

    public float getKnockBackBase() {
        return knockBackBase;
    }

    public VirtualEntityFactory withKnockBackBase(float knockBackBase) {
        this.knockBackBase = knockBackBase;
        return this;
    }

    public float getKnockBackPerDamage() {
        return knockBackPerDamage;
    }

    public VirtualEntityFactory withKnockBackPerDamage(float knockBackPerDamage) {
        this.knockBackPerDamage = knockBackPerDamage;
        return this;
    }

    public float getProjectileDamage() {
        return projectileDamage;
    }

    public VirtualEntityFactory withProjectileDamage(float projectileDamage) {
        this.projectileDamage = projectileDamage;
        return this;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public VirtualEntityFactory withViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
        return this;
    }

    public float getMaxRotationStep() {
        return maxRotationStep;
    }

    public VirtualEntityFactory withMaxRotationStep(float maxRotationStep) {
        this.maxRotationStep = maxRotationStep;
        return this;
    }

    public float getMaxRotationStepFlight() {
        return maxRotationStepFlight;
    }

    public VirtualEntityFactory withMaxRotationStepFlight(float maxRotationStepFlight) {
        this.maxRotationStepFlight = maxRotationStepFlight;
        return this;
    }

    public double getHealth() {
        return health;
    }

    public VirtualEntityFactory withHealth(double health) {
        this.health = health;
        return this;
    }

    public Set<UUID> getWhitelist() {
        return whitelist;
    }

    public VirtualEntityFactory withWhitelist(Set<UUID> whitelist) {
        this.whitelist = whitelist;
        return this;
    }

    public Set<McmeEntity> getEnemies() {
        return enemies;
    }

    public VirtualEntityFactory withEnemies(Set<McmeEntity> enemies) {
        this.enemies = enemies;
        return this;
    }

    public McmeEntity getShooter() {
        return shooter;
    }

    public VirtualEntityFactory withShooter(McmeEntity shooter) {
        this.shooter = shooter;
        return this;
    }

    public float getProjectileVelocity() {
        return projectileVelocity;
    }

    public VirtualEntityFactory withProjectileVelocity(float velocity) {
        this.projectileVelocity = velocity;
        return this;
    }

    public Vector getAttackPoint() {
        return attackPoint;
    }

    public VirtualEntityFactory withAttackPoint(Vector attackPoint) {
        this.attackPoint = attackPoint;
        return this;
    }

    public Vector getSaddlePoint() {
        return saddlePoint;
    }

    public VirtualEntityFactory withSaddlePoint(Vector saddlePoint) {
        this.saddlePoint = saddlePoint;
        return this;
    }

    public Vector getSitPoint() {
        return sitPoint;
    }

    public VirtualEntityFactory withSitPoint(Vector sitPoint) {
        this.sitPoint = sitPoint;
        return this;
    }

    public boolean isSaddled() {
        return saddled;
    }

    public VirtualEntityFactory withSaddled(boolean saddled) {
        this.saddled = saddled;
        return this;
    }

    public Horse.Color getHorseColor() {
        return horseColor;
    }

    public VirtualEntityFactory withHorseColor(Horse.Color horseColor) {
        this.horseColor = horseColor;
        return this;
    }

    public Horse.Style getHorseStyle() {
        return horseStyle;
    }

    public VirtualEntityFactory withHorseStyle(Horse.Style horseStyle) {
        this.horseStyle = horseStyle;
        return this;
    }

    public org.bukkit.entity.Entity getDependingEntity() {
        return dependingEntity;
    }

    public VirtualEntityFactory withDependingEntity(org.bukkit.entity.Entity dependingEntity) {
        this.dependingEntity = dependingEntity;
        return this;
    }

    public int getAttackDelay() {
        return attackDelay;
    }

    public VirtualEntityFactory withAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
        return this;
    }

    public static VirtualEntityFactory getDefaults() {
        return new VirtualEntityFactory(null,null,false,
                                         null ,"",null);
    }

    public boolean isWriteDefaultValuesToFile() {
        return writeDefaultValuesToFile;
    }

    public VirtualEntityFactory withWriteDefaultValuesToFile(boolean writeDefaultValuesToFile) {
        this.writeDefaultValuesToFile = writeDefaultValuesToFile;
        return this;
    }

    /**
     * For internal use by the entity server only.
     * @param entityId
     * @return
     * @throws InvalidLocationException
     */
    public McmeEntity build(int entityId) throws InvalidLocationException, InvalidDataException {
//Logger.getGlobal().info("UUID: "+uniqueId.toString());
        if(type.isCustomType()) {
            switch(type.getCustomType()) {
                case BAKED_ANIMATION:
                    return new BakedAnimationEntity(entityId, this);
                case WINGED_FLIGHT:
                    return new WingedFlightEntity(entityId, this);
                default:
                    throw new RuntimeException("EntityType not implemented");
            }
        } else {
            switch(type.getBukkitEntityType()) {
                case EXPERIENCE_ORB:
                case PAINTING:
                case PRIMED_TNT:
                    throw new RuntimeException("EntityType not implemented");
                case PLAYER:
                    return new SimplePlayer(entityId, this);
                case ARROW:
                case SPECTRAL_ARROW:
                case SNOWBALL:
                case EGG:
                case SPLASH_POTION:
                case THROWN_EXP_BOTTLE:
                case TRIDENT:
                case ENDER_PEARL:
                case FIREWORK:
                case FALLING_BLOCK:
                case LLAMA_SPIT:
                    return new Projectile(entityId,this);
                case AREA_EFFECT_CLOUD:
                case ARMOR_STAND:
                case BOAT:
                case DRAGON_FIREBALL:
                case ENDER_CRYSTAL:
                case EVOKER:
                case IRON_GOLEM:
                case ITEM_FRAME:
                case FIREBALL:
                case LEASH_HITCH:
                case LIGHTNING:
                case MINECART:
                case MINECART_CHEST:
                case MINECART_COMMAND:
                case MINECART_FURNACE:
                case MINECART_HOPPER:
                case MINECART_MOB_SPAWNER:
                case MINECART_TNT:
                case SHULKER_BULLET:
                case SMALL_FIREBALL:
                case WITHER_SKULL:
                case FISHING_HOOK:
                    return new SimpleNonLivingEntity(entityId, this);
                case HORSE:
                    return new SimpleHorse(entityId, this);
                default:
                    return new SimpleLivingEntity(entityId, this);
            }
        }
    }

}
