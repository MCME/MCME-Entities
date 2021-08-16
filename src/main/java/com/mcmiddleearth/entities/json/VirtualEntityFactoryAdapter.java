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

    @Override
    public void write(JsonWriter out, VirtualEntityFactory factory) throws IOException {
        VirtualEntityFactory defaults = VirtualEntityFactory.getDefaults();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        out.beginObject();
        try {
            out.name("type").value(factory.getType().name());
            JsonUtil.writeNonDefaultBoolean(out, "blacklist", factory.hasBlackList(), defaults.hasBlackList());
            if (factory.getWhitelist() != null) {
                out.name("whitelist").beginArray();
                for (UUID uuid : factory.getWhitelist()) out.value(uuid.toString());
                out.endArray();
            }
            JsonUtil.writeNonDefaultString(out, "uniqueId", factory.getUniqueId().toString(), defaults.getUniqueId().toString());
            JsonUtil.writeNonDefaultString(out, "name", factory.getName(), defaults.getName());
            JsonUtil.writeNonDefaultString(out, "datafile", factory.getDataFile(), defaults.getDataFile());
            JsonUtil.writeNonDefaultString(out, "displayName", factory.getDisplayName(), defaults.getDisplayName());
            JsonUtil.writeNonDefaultVector(out, "displayNamePosition", factory.getDisplayNamePosition(), defaults.getDisplayNamePosition(), gson);
            if (factory.getSpawnLocationEntity() != null) {
                out.name("spawnLocationEntity");
                JsonUtil.writeEntityLink(factory.getSpawnLocationEntity(), true, out);
            } else {
                out.name("spawnLocation");
                gson.toJson(factory.getLocation(), Location.class, out);
            }
            JsonUtil.writeNonDefaultFloat(out, "roll", factory.getRoll(), defaults.getRoll());
            JsonUtil.writeNonDefaultFloat(out, "headYaw", factory.getHeadYaw(), defaults.getHeadYaw());
            JsonUtil.writeNonDefaultFloat(out, "headPitch", factory.getHeadPitch(), defaults.getHeadPitch());
            JsonUtil.writeNonDefaultInt(out, "health", factory.getHealth(), defaults.getHealth());
            JsonUtil.writeNonDefaultString(out, "movementType", factory.getMovementType().name().toLowerCase(),
                                                                      defaults.getMovementType().name().toLowerCase());
            if (!factory.getAttributes().isEmpty()) {
                out.name("attributes").beginArray();
                for (AttributeInstance attributeInstance : factory.getAttributes().values()) {
                    JsonUtil.writeNonDefaultAttribute(out, attributeInstance, factory.getType(), gson);
                }
                out.endArray();
            }
            if (!factory.getBoundingBox().equals(defaults.getBoundingBox())) {
                out.name("boundingBox");
                gson.toJson(factory.getBoundingBox(), EntityBoundingBox.class, out);
            }
            if (factory.getGoalFactory() != null) {
                out.name("goalFactory");
                gson.toJson(factory.getGoalFactory(), VirtualEntityGoalFactory.class, out);
            }
            JsonUtil.writeNonDefaultVector(out, "headPitchCenter", factory.getHeadPitchCenter(), defaults.getHeadPitchCenter(), gson);
            if (!factory.getSpeechBalloonLayout().equals(defaults.getSpeechBalloonLayout())) {
                out.name("speechBalloonLayout");
                gson.toJson(factory.getSpeechBalloonLayout(), SpeechBalloonLayout.class, out);
            }
            JsonUtil.writeNonDefaultVector(out, "mouth", factory.getMouth(), defaults.getMouth(), gson);
            JsonUtil.writeNonDefaultBoolean(out, "manualAnimation", factory.getManualAnimationControl(), defaults.getManualAnimationControl());
            JsonUtil.writeNonDefaultInt(out, "headPoseDelay", factory.getHeadPoseDelay(), defaults.getHeadPoseDelay());
            JsonUtil.writeNonDefaultInt(out, "viewDistance", factory.getViewDistance(), defaults.getViewDistance());
            JsonUtil.writeNonDefaultFloat(out, "maxRotationStep", factory.getMaxRotationStep(), defaults.getMaxRotationStep());
            JsonUtil.writeNonDefaultFloat(out, "maxRotationStepFlight", factory.getMaxRotationStepFlight(), defaults.getMaxRotationStepFlight());
            JsonUtil.writeNonDefaultInt(out, "updateInterval", factory.getUpdateInterval(), defaults.getUpdateInterval());
            JsonUtil.writeNonDefaultInt(out, "jumpHeight", factory.getJumpHeight(), defaults.getJumpHeight());
            JsonUtil.writeNonDefaultFloat(out, "knockBackBase", factory.getKnockBackBase(), defaults.getKnockBackBase());
            JsonUtil.writeNonDefaultFloat(out, "knockBackPerDamage", factory.getKnockBackPerDamage(), defaults.getKnockBackPerDamage());
            if (!factory.getEnemies().isEmpty()) {
                out.name("enemies").beginArray();
                try {
                    for (McmeEntity enemy : factory.getEnemies()) {
                        JsonUtil.writeEntityLink(enemy, false, out);
                    }
                } finally { out.endArray(); }
            }
        } finally { out.endObject(); }
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
                    case "type":
                        factory.withEntityType(McmeEntityType.valueOf(in.nextString().toUpperCase()));
                        break;
                    case "blacklist":
                        factory.withBlackList(in.nextBoolean());
                        break;
                    case "whitelist":
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
                    case "uniqueId":
                        factory.withUuid(UUID.fromString(in.nextString()));
                        break;
                    case "name":
                        factory.withName(in.nextString());
                        break;
                    case "datafile":
                        factory.withDataFile(in.nextString());
                        break;
                    case "displayName":
                        factory.withDisplayName(in.nextString());
                        break;
                    case "displayNamePosition":
                        factory.withDisplayNamePosition(gson.fromJson(in, Vector.class));
                        break;
                    case "spawnLocationEntity":
                        factory.useEntityForSpawnLocation(JsonUtil.readEntityLink(in));
                        break;
                    case "spawnLocation":
                        factory.withLocation(gson.fromJson(in,Location.class));
                        break;
                    case "roll":
                        factory.withRoll((float)in.nextDouble());
                        break;
                    case "headYaw":
                        factory.withHeadYaw((float)in.nextDouble());
                        break;
                    case "headPitch":
                        factory.withHeadPitch((float)in.nextDouble());
                        break;
                    case "health":
                        factory.withHealth(in.nextInt());
                        break;
                    case "movementType":
                        factory.withMovementType(MovementType.valueOf(in.nextString().toUpperCase()));
                        break;
                    case "attributes":
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
                    case "boundingBox":
                        factory.withBoundingBox(gson.fromJson(in, EntityBoundingBox.class));
                        break;
                    case "goalFactory":
                        factory.withGoalFactory(gson.fromJson(in,VirtualEntityGoalFactory.class));
                        break;
                    case "headPitchCenter":
                        factory.withHeadPitchCenter(gson.fromJson(in, Vector.class));
                        break;
                    case "speechBalloonLayout":
                        factory.withSpeechBalloonLayout(gson.fromJson(in, SpeechBalloonLayout.class));
                        break;
                    case "mouth":
                        factory.withMouth(gson.fromJson(in, Vector.class));
                        break;
                    case "manualAnimation":
                        factory.withManualAnimationControl(in.nextBoolean());
                        break;
                    case "headPoseDelay":
                        factory.withHeadPoseDelay(in.nextInt());
                        break;
                    case "viewDistance":
                        factory.withViewDistance(in.nextInt());
                        break;
                    case "maxRotationStep":
                        factory.withMaxRotationStep((float)in.nextDouble());
                        break;
                    case "maxRotationStepFlight":
                        factory.withMaxRotationStepFlight((float)in.nextDouble());
                        break;
                    case "updateInterval":
                        factory.withUpdateInterval(in.nextInt());
                        break;
                    case "jumpHeight":
                        factory.withJumpHeight(in.nextInt());
                        break;
                    case "knockBackBase":
                        factory.withKnockBackBase((float)in.nextDouble());
                        break;
                    case "knockBackPerDamage":
                        factory.withKnockBackPerDamage((float)in.nextDouble());
                        break;
                    case "enemies":
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
