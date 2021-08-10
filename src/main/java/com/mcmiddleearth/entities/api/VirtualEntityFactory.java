package com.mcmiddleearth.entities.api;

import com.mcmiddleearth.entities.ai.goal.GoalType;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.entities.*;
import com.mcmiddleearth.entities.entities.attributes.VirtualAttributeFactory;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.util.UuidGenerator;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * VirtualEntityFactory class is used to provide all required information during spawn process of an entity.
 */
public class VirtualEntityFactory {

    private McmeEntityType type;

    private boolean invertWhitelist;

    private UUID uniqueId;

    private String name, dataFile, displayName;

    private Vector displayNamePosition = new Vector(0,2,0);

    private Location location;

    private Entity spawnLocationEntity = null;

    private MovementType movementType = MovementType.UPRIGHT;

    private final Map<Attribute, AttributeInstance> attributes;

    private EntityBoundingBox boundingBox;

    private GoalType goalType;

    private Location targetLocation;

    private Location[] checkpoints;

    private McmeEntity targetEntity;

    private Vector headPitchCenter = new Vector(0,0.1,-0.03);

    private SpeechBalloonLayout speechBalloonLayout = new SpeechBalloonLayout(SpeechBalloonLayout.Position.RIGHT,
                                                                              SpeechBalloonLayout.Width.OPTIMAL);

    private Vector mouth = new Vector(0,1.7,0);

    private boolean manualAnimationControl = false;

    private int headPoseDelay = 2;

    public VirtualEntityFactory(McmeEntityType type, Location location) {
        invertWhitelist = false;
        uniqueId = UuidGenerator.fast_random();//getRandomV2();
        this.type = type;
        this.location = location;
        attributes = VirtualAttributeFactory.getAttributesFor(type);
        boundingBox = EntityBoundingBox.getBoundingBox(type);
    }

    private VirtualEntityFactory(McmeEntityType type, Location location, boolean invertWhitelist,
                                 UUID uniqueId, String name, Map<Attribute, AttributeInstance> attributes) {
        this.type = type;
        this.location = location;
        this.invertWhitelist = invertWhitelist;
        this.uniqueId = uniqueId;
        this.name = name;
        this.attributes = attributes;
    }

    public static Collection<String> availableProperties() {
        return Stream.of("type","invertWhitelist","uniqueId", "name", "dataFile", "displayName","displayNamePosition",
                "location","movementType","goalType","targetLocation","targetEntity","headPitchCenter",
                "speechBalloonLayout","mouth","manualAnimation","headPoseDelay").map(String::toLowerCase)
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
        this.invertWhitelist = useBlacklist;
        return this;
    }

    public VirtualEntityFactory withAttribute(Attribute attribute, double baseValue) {
        attributes.put(attribute, VirtualAttributeFactory.getAttributeInstance(attribute,baseValue));
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

    public VirtualEntityFactory withGoalType(GoalType goalType) {
        this.goalType = goalType;
        return this;
    }

    public VirtualEntityFactory withTargetLocation(Location target) {
        this.targetLocation = target;
        return this;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public VirtualEntityFactory withTargetEntity(McmeEntity target) {
        this.targetEntity = target;
        return this;
    }

    public McmeEntity getTargetEntity() {
        return targetEntity;
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

    public VirtualEntityFactory useEntityForSpawnLocation(Entity entity) {
        this.spawnLocationEntity = entity;
        return this;
    }

    public VirtualEntityFactory withSpeechBalloonLayout(SpeechBalloonLayout layout) {
        this.speechBalloonLayout = layout;
        return this;
    }

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

    public VirtualEntityGoalFactory getGoalFactory() {
        return new VirtualEntityGoalFactory().withTargetEntity(targetEntity)
                .withTargetLocation(targetLocation)
                .withCheckpoints(checkpoints)
                .withGoalType(goalType);
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

    public VirtualEntityFactory withInvertWhiteList(boolean invert) {
        this.invertWhitelist = invert;
        return this;
    }

    public boolean isInvertWhitelist() {
        return invertWhitelist;
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

    public SpeechBalloonLayout getSpeechBalloonLayout() { return speechBalloonLayout; }

    public Vector getMouth() { return mouth; }

    public VirtualEntityFactory withCheckpoints(Location[] checkpoints) {
        this.checkpoints = checkpoints;
        return this;
    }

    public Location[] getCheckpoints() {
        return checkpoints;
    }

    /**
     * Attributes are not yet implemented.
     * @return
     */
    public Map<Attribute, AttributeInstance> getAttributes() {
        Map<Attribute,AttributeInstance> result = new HashMap<>();
        attributes.forEach((attribute, instance)
                -> result.put(attribute, new VirtualEntityAttributeInstance(attribute,
                                                                            instance.getDefaultValue(),
                                                                            instance.getBaseValue())));
        return result;
    }

    /**
     * For internal use by the entity server only.
     * @param entityId
     * @return
     * @throws InvalidLocationException
     */
    public McmeEntity build(int entityId) throws InvalidLocationException, InvalidDataException {
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
                case AREA_EFFECT_CLOUD:
                case ARMOR_STAND:
                case ARROW:
                case BOAT:
                case DRAGON_FIREBALL:
                case ENDER_CRYSTAL:
                case EVOKER:
                case ENDER_PEARL:
                case FALLING_BLOCK:
                case FIREWORK:
                case IRON_GOLEM:
                case ITEM_FRAME:
                case FIREBALL:
                case LEASH_HITCH:
                case LIGHTNING:
                case LLAMA_SPIT:
                case MINECART:
                case MINECART_CHEST:
                case MINECART_COMMAND:
                case MINECART_FURNACE:
                case MINECART_HOPPER:
                case MINECART_MOB_SPAWNER:
                case MINECART_TNT:
                case SHULKER_BULLET:
                case SMALL_FIREBALL:
                case SNOWBALL:
                case SPECTRAL_ARROW:
                case EGG:
                case THROWN_EXP_BOTTLE:
                case SPLASH_POTION:
                case TRIDENT:
                case WITHER_SKULL:
                case FISHING_HOOK:
                    return new SimpleNonLivingEntity(entityId, this);
                default:
                    return new SimpleLivingEntity(entityId, this);
            }
        }
    }

}
