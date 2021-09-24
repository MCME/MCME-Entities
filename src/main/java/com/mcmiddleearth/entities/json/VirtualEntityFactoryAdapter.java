package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class VirtualEntityFactoryAdapter extends TypeAdapter<VirtualEntityFactory> {

    private static final String
    TYPE                = "type",
    BLACKLIST           = "blacklist",
    WHITELIST           = "whitelist",
    UNIQUE_ID           = "unique_id",
    NAME                = "name",
    DATA_FILE           = "data_file",
    DISPLAY_NAME        = "display_name",
    DISPLAY_NAME_POSITION   = "display_name_position",
    SPAWN_LOCATION_ENTITY   = "spawn_location_entity",
    SPAWN_LOCATION          = "spawn_location",
    ROLL                    = "roll",
    HEAD_YAW                = "head_yaw",
    HEAD_PITCH              = "head_pitch",
    HEALTH                  = "health",
    MOVEMENT_TYPE           = "movement_type",
    ATTRIBUTES              = "attributes",
    BOUNDING_BOX            = "bounding_box",
    GOAL_FACTORY            = "goal_factory",
    HEAD_PITCH_CENTER       = "head_pitch_center",
    SPEECH_BALLOON_LAYOUT   = "speech_balloon_layout",
    MOUTH                   = "mouth",
    MANUAL_ANIMATION        = "manual_animation",
    HEAD_POSE_DELAY         = "head_pose_delay",
    VIEW_DISTANCE           = "view_distance",
    MAX_ROTATION_STEP       = "max_rotation_step",
    MAX_ROTATION_STEP_FLIGHT    = "max_rotation_step_flight",
    UPDATE_INTERVAL             = "update_interval",
    JUMP_HEIGHT                 = "jump_height",
    KNOCK_BACK_BASE             = "knock_back_base",
    KNOCK_BACK_PER_DAMAGE       = "knock_back_per_damage",
    ENEMIES                     = "enemies";
    

    @Override
    public void write(JsonWriter out, VirtualEntityFactory factory) throws IOException {
        boolean writeDefaults = factory.isWriteDefaultValuesToFile();
        VirtualEntityFactory defaults = VirtualEntityFactory.getDefaults();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        out.beginObject();
        out.name(TYPE).value(factory.getType().name());
        JsonUtil.writeNonDefaultBoolean(out, BLACKLIST, factory.hasBlackList(), defaults.hasBlackList(), writeDefaults);
        if (writeDefaults || factory.getWhitelist() != null) {
            out.name(WHITELIST).beginArray();
            if(factory.getWhitelist()!=null) {
                for (UUID uuid : factory.getWhitelist()) out.value(uuid.toString());
            }
            out.endArray();
        }
        JsonUtil.writeNonDefaultUuid(out, UNIQUE_ID, factory.getUniqueId(), defaults.getUniqueId(),writeDefaults);
        JsonUtil.writeNonDefaultString(out, NAME, factory.getName(), defaults.getName(),writeDefaults);
        JsonUtil.writeNonDefaultString(out, DATA_FILE, factory.getDataFile(), defaults.getDataFile(),writeDefaults);
        JsonUtil.writeNonDefaultString(out, DISPLAY_NAME, factory.getDisplayName(), defaults.getDisplayName(),writeDefaults);
        JsonUtil.writeNonDefaultVector(out, DISPLAY_NAME_POSITION, factory.getDisplayNamePosition(), defaults.getDisplayNamePosition(), gson, writeDefaults);
        if (writeDefaults || factory.getSpawnLocationEntity() != null) {
            out.name(SPAWN_LOCATION_ENTITY);
            JsonUtil.writeEntityLink(factory.getSpawnLocationEntity(), true, out);
        } else {
            out.name(SPAWN_LOCATION);
            gson.toJson(factory.getLocation(), Location.class, out);
        }
        JsonUtil.writeNonDefaultFloat(out, ROLL, factory.getRoll(), defaults.getRoll(),writeDefaults);
        JsonUtil.writeNonDefaultFloat(out, HEAD_YAW, factory.getHeadYaw(), defaults.getHeadYaw(),writeDefaults);
        JsonUtil.writeNonDefaultFloat(out, HEAD_PITCH, factory.getHeadPitch(), defaults.getHeadPitch(),writeDefaults);
        JsonUtil.writeNonDefaultDouble(out, HEALTH, factory.getHealth(), defaults.getHealth(),writeDefaults);
        JsonUtil.writeNonDefaultString(out, MOVEMENT_TYPE, factory.getMovementType().name().toLowerCase(),
                                                                  defaults.getMovementType().name().toLowerCase(),writeDefaults);
        if (writeDefaults || !factory.getAttributes().isEmpty()) {
            out.name(ATTRIBUTES).beginArray();
            for (AttributeInstance attributeInstance : factory.getAttributes().values()) {
                JsonUtil.writeNonDefaultAttribute(out, attributeInstance, factory.getType(), gson, writeDefaults);
            }
            out.endArray();
        }
        if (writeDefaults || !factory.getBoundingBox().equals(defaults.getBoundingBox())) {
            out.name(BOUNDING_BOX);
            gson.toJson(factory.getBoundingBox(), EntityBoundingBox.class, out);
        }
        if (writeDefaults || factory.getGoalFactory() != null) {
            out.name(GOAL_FACTORY);
            VirtualEntityGoalFactory goalFactory = factory.getGoalFactory();
            if(writeDefaults) goalFactory.withWriteDefaultsToFile(true);
            gson.toJson(goalFactory, VirtualEntityGoalFactory.class, out);
        }
        JsonUtil.writeNonDefaultVector(out, HEAD_PITCH_CENTER, factory.getHeadPitchCenter(),
                                        defaults.getHeadPitchCenter(), gson,writeDefaults);
        if (writeDefaults || !factory.getSpeechBalloonLayout().equals(defaults.getSpeechBalloonLayout())) {
            out.name(SPEECH_BALLOON_LAYOUT);
            gson.toJson(factory.getSpeechBalloonLayout(), SpeechBalloonLayout.class, out);
        }
        JsonUtil.writeNonDefaultVector(out, MOUTH, factory.getMouth(), defaults.getMouth(), gson,writeDefaults);
        JsonUtil.writeNonDefaultBoolean(out, MANUAL_ANIMATION, factory.getManualAnimationControl(),
                                        defaults.getManualAnimationControl(),writeDefaults);
        JsonUtil.writeNonDefaultInt(out, HEAD_POSE_DELAY, factory.getHeadPoseDelay(),
                                        defaults.getHeadPoseDelay(),writeDefaults);
        JsonUtil.writeNonDefaultInt(out, VIEW_DISTANCE, factory.getViewDistance(),
                                        defaults.getViewDistance(),writeDefaults);
        JsonUtil.writeNonDefaultFloat(out, MAX_ROTATION_STEP, factory.getMaxRotationStep(),
                                        defaults.getMaxRotationStep(),writeDefaults);
        JsonUtil.writeNonDefaultFloat(out, MAX_ROTATION_STEP_FLIGHT, factory.getMaxRotationStepFlight(),
                                        defaults.getMaxRotationStepFlight(),writeDefaults);
        JsonUtil.writeNonDefaultInt(out, UPDATE_INTERVAL, factory.getUpdateInterval(),
                                        defaults.getUpdateInterval(),writeDefaults);
        JsonUtil.writeNonDefaultInt(out, JUMP_HEIGHT, factory.getJumpHeight(),
                                        defaults.getJumpHeight(),writeDefaults);
        JsonUtil.writeNonDefaultFloat(out, KNOCK_BACK_BASE, factory.getKnockBackBase(),
                                        defaults.getKnockBackBase(),writeDefaults);
        JsonUtil.writeNonDefaultFloat(out, KNOCK_BACK_PER_DAMAGE, factory.getKnockBackPerDamage(),
                                        defaults.getKnockBackPerDamage(),writeDefaults);
        if (writeDefaults || !factory.getEnemies().isEmpty()) {
            out.name(ENEMIES).beginArray();
            for (McmeEntity enemy : factory.getEnemies()) {
                JsonUtil.writeEntityLink(enemy, false, out);
            }
            out.endArray();
        }
        out.endObject();
    }

    @Override
    public VirtualEntityFactory read(JsonReader in) throws IOException {
        VirtualEntityFactory factory = VirtualEntityFactory.getDefaults();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        in.beginObject();
        while(in.hasNext()) {
            String key = in.nextName();
            try {
                switch (key) {
                    case TYPE:
                        factory.withEntityType(McmeEntityType.valueOf(in.nextString().toUpperCase()));
                        break;
                    case BLACKLIST:
                        factory.withBlackList(in.nextBoolean());
                        break;
                    case WHITELIST:
                        Set<UUID> whitelist = new HashSet<>();
                        in.beginArray();
                        //try {
                            while (in.hasNext()) {
                                whitelist.add(UUID.fromString(in.nextString()));
                            }
                        //} finally {
                        in.endArray();//}
                        factory.withWhitelist(whitelist);
                        break;
                    case UNIQUE_ID:
                        factory.withUuid(UUID.fromString(in.nextString()));
                        break;
                    case NAME:
                        factory.withName(in.nextString());
                        break;
                    case DATA_FILE:
                        factory.withDataFile(in.nextString());
                        break;
                    case DISPLAY_NAME:
                        factory.withDisplayName(in.nextString());
                        break;
                    case DISPLAY_NAME_POSITION:
                        factory.withDisplayNamePosition(gson.fromJson(in, Vector.class));
                        break;
                    case SPAWN_LOCATION_ENTITY:
                        factory.useEntityForSpawnLocation(JsonUtil.readEntityLink(in));
                        break;
                    case SPAWN_LOCATION:
                        factory.withLocation(gson.fromJson(in,Location.class));
                        break;
                    case ROLL:
                        factory.withRoll((float)in.nextDouble());
                        break;
                    case HEAD_YAW:
                        factory.withHeadYaw((float)in.nextDouble());
                        break;
                    case HEAD_PITCH:
                        factory.withHeadPitch((float)in.nextDouble());
                        break;
                    case HEALTH:
                        factory.withHealth(in.nextInt());
                        break;
                    case MOVEMENT_TYPE:
                        factory.withMovementType(MovementType.valueOf(in.nextString().toUpperCase()));
                        break;
                    case ATTRIBUTES:
                        Map<Attribute,AttributeInstance> attributes = new HashMap<>();
                        in.beginArray();
                        //try {
                            while(in.hasNext()) {
                                VirtualEntityAttributeInstance instance = gson.fromJson(in,VirtualEntityAttributeInstance.class);
                                attributes.put(instance.getAttribute(),instance);
                            }
                        //} finally {
                        in.endArray(); //}
                        factory.withAttributes(attributes);
                        break;
                    case BOUNDING_BOX:
                        factory.withBoundingBox(gson.fromJson(in, EntityBoundingBox.class));
                        break;
                    case GOAL_FACTORY:
                        factory.withGoalFactory(gson.fromJson(in,VirtualEntityGoalFactory.class));
                        break;
                    case HEAD_PITCH_CENTER:
                        factory.withHeadPitchCenter(gson.fromJson(in, Vector.class));
                        break;
                    case SPEECH_BALLOON_LAYOUT:
                        factory.withSpeechBalloonLayout(gson.fromJson(in, SpeechBalloonLayout.class));
                        break;
                    case MOUTH:
                        factory.withMouth(gson.fromJson(in, Vector.class));
                        break;
                    case MANUAL_ANIMATION:
                        factory.withManualAnimationControl(in.nextBoolean());
                        break;
                    case HEAD_POSE_DELAY:
                        factory.withHeadPoseDelay(in.nextInt());
                        break;
                    case VIEW_DISTANCE:
                        factory.withViewDistance(in.nextInt());
                        break;
                    case MAX_ROTATION_STEP:
                        factory.withMaxRotationStep((float)in.nextDouble());
                        break;
                    case MAX_ROTATION_STEP_FLIGHT:
                        factory.withMaxRotationStepFlight((float)in.nextDouble());
                        break;
                    case UPDATE_INTERVAL:
                        factory.withUpdateInterval(in.nextInt());
                        break;
                    case JUMP_HEIGHT:
                        factory.withJumpHeight(in.nextInt());
                        break;
                    case KNOCK_BACK_BASE:
                        factory.withKnockBackBase((float)in.nextDouble());
                        break;
                    case KNOCK_BACK_PER_DAMAGE:
                        factory.withKnockBackPerDamage((float)in.nextDouble());
                        break;
                    case ENEMIES:
                        Set<McmeEntity> enemies = new HashSet<>();
                        in.beginArray();
                        //try {
                            while(in.hasNext()) {
                                enemies.add(JsonUtil.readEntityLink(in));
                            }
                        //} finally {
                        in.endArray(); //}
                        factory.withEnemies(enemies);
                        break;
                    default:
                        in.skipValue();
                }
            } catch (IllegalArgumentException | IllegalStateException | JsonSyntaxException ex) {
                //Logger.getLogger(VirtualEntityFactoryAdapter.class.getSimpleName()).warning("Error reading key: "+key+" -> "+ex.getMessage());
                throw new IllegalArgumentException("Error reading key: "+key+" at "+in.getPath() + " -> "+ex.getMessage());
            }
        }
        in.endObject();
        return factory;
    }
}
