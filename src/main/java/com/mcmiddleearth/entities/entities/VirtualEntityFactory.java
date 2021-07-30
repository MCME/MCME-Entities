package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.ai.goal.GoalType;
import com.mcmiddleearth.entities.ai.goal.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.ai.movement.MovementType;
import com.mcmiddleearth.entities.entities.attributes.VirtualAttributeFactory;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.entities.composite.SpeechBalloonLayout;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.util.UuidGenerator;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VirtualEntityFactory {

    private final McmeEntityType type;

    private boolean invertWhitelist;

    private UUID uniqueId;

    private String name, dataFile, displayName;

    private Vector displayNamePosition = new Vector(0,2,0);

    private Location location;

    private MovementType movementType = MovementType.WALKING;

    private final Map<Attribute, AttributeInstance> attributes;

    private EntityBoundingBox boundingBox;

    private GoalType goalType;

    private Location targetLocation;

    private McmeEntity targetEntity;

    private SpeechBalloonLayout speechBalloonLayout = new SpeechBalloonLayout(SpeechBalloonLayout.Position.RIGHT,
                                                                              SpeechBalloonLayout.Width.OPTIMAL);

    private Vector mouth = new Vector(0,1.8,0);

    public VirtualEntityFactory(McmeEntityType type, Location location) {
        invertWhitelist = false;
        uniqueId = UuidGenerator.getRandomV2();
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

    public VirtualEntityFactory withTargetEntity(McmeEntity target) {
        this.targetEntity = target;
        return this;
    }

    public VirtualEntityFactory withDataFile(String filename) {
        this.dataFile = filename;
        return this;
    }

    public VirtualEntityFactory withLocation(Location location) {
        this.location = location;
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

    public String getDataFile() {
        return this.dataFile;
    }

    public VirtualEntityGoalFactory getGoalFactory() {
        return new VirtualEntityGoalFactory().withTargetEntity(targetEntity)
                .withTargetLocation(targetLocation)
                .withGoalType(goalType);
    }

    public EntityBoundingBox getBoundingBox() {
        return new EntityBoundingBox(boundingBox);
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public McmeEntityType getType() {
        return type;
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
        return location.clone();
    }

    public SpeechBalloonLayout getSpeechBalloonLayout() { return speechBalloonLayout; }

    public Vector getMouth() { return mouth; }

    public Map<Attribute, AttributeInstance> getAttributes() {
        Map<Attribute,AttributeInstance> result = new HashMap<>();
        attributes.forEach((attribute, instance)
                -> result.put(attribute, new VirtualEntityAttributeInstance(attribute,
                                                                            instance.getDefaultValue(),
                                                                            instance.getBaseValue())));
        return result;
    }

    public McmeEntity build(int entityId) throws InvalidLocationException {
        if(type.isCustomType()) {
            switch(type.getCustomType()) {
                case BAKED_ANIMATION:
                    return new BakedAnimationEntity(entityId, this);
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
