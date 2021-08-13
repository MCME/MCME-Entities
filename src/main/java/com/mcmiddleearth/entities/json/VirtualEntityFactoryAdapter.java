package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.api.*;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.attributes.VirtualAttributeFactory;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class VirtualEntityFactoryAdapter extends TypeAdapter<VirtualEntityFactory> {

    @Override
    public void write(JsonWriter out, VirtualEntityFactory factory) throws IOException {
        VirtualEntityFactory defaults = VirtualEntityFactory.getDefaults();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        out.beginObject()
            .name("type").value(factory.getType().name());
            JsonUtil.writeNonDefaultBoolean(out,"blacklist",factory.hasBlackList(),defaults.hasBlackList());
            if(factory.getWhitelist() != null) {
                out.name("whitelist").beginArray();
                for(UUID uuid : factory.getWhitelist()) out.value(uuid.toString());
                out.endArray();
            }
            JsonUtil.writeNonDefaultString(out,"uniqueid",factory.getUniqueId().toString(),defaults.getUniqueId().toString());
            JsonUtil.writeNonDefaultString(out,"name",factory.getName(),defaults.getName());
            JsonUtil.writeNonDefaultString(out,"datafile",factory.getDataFile(),defaults.getDataFile());
            JsonUtil.writeNonDefaultString(out,"displayName",factory.getDisplayName(),defaults.getDisplayName());
            JsonUtil.writeNonDefaultVector(out,"displayNamePosition",factory.getDisplayNamePosition(),defaults.getDisplayNamePosition(),gson);
            if(factory.getSpawnLocationEntity()!=null) {
                out.name("spawnLocationEntity");
                JsonUtil.writeEntityLink(factory.getSpawnLocationEntity(),true,out);
            } else {
                out.name("spawnLocation");
                gson.toJson(factory.getLocation(),Location.class,out);
            }
            JsonUtil.writeNonDefaultFloat(out,"roll",factory.getRoll(),defaults.getRoll());
            JsonUtil.writeNonDefaultFloat(out,"headYaw",factory.getHeadYaw(),defaults.getHeadYaw());
            JsonUtil.writeNonDefaultFloat(out,"headPitch",factory.getHeadPitch(),defaults.getHeadPitch());
            JsonUtil.writeNonDefaultInt(out,"health",factory.getHealth(),defaults.getHealth());
            JsonUtil.writeNonDefaultString(out,"movementType",factory.getMovementType().name(),defaults.getMovementType().name());
            if(!factory.getAttributes().isEmpty()) {
                out.name("attributes").beginArray();
                for (AttributeInstance attributeInstance : factory.getAttributes().values()) {
                    JsonUtil.writeNonDefaultAttribute(out, attributeInstance, factory.getType(),gson);
                }
                out.endArray();
            }
            if(!factory.getBoundingBox().equals(defaults.getBoundingBox())) {
                out.name("boundingBox");
                gson.toJson(factory.getBoundingBox(), EntityBoundingBox.class, out);
            }
            if(factory.getGoalFactory()!=null) {
                out.name("goalFactory");
                gson.toJson(factory.getGoalFactory(),VirtualEntityGoalFactory.class,out);
            }
           JsonUtil.writeNonDefaultVector(out,"headPitchCenter",factory.getHeadPitchCenter(),defaults.getHeadPitchCenter(),gson);
            if(!factory.getSpeechBalloonLayout().equals(defaults.getSpeechBalloonLayout())) {
                out.name("speechBalloonLayout");
                gson.toJson(factory.getSpeechBalloonLayout(),SpeechBalloonLayout.class,out);
            }
            JsonUtil.writeNonDefaultVector(out,"mouth",factory.getMouth(),defaults.getMouth(),gson);
            JsonUtil.writeNonDefaultBoolean(out,"manualAnimation",factory.getManualAnimationControl(),defaults.getManualAnimationControl());
            JsonUtil.writeNonDefaultInt(out, "headPoseDelay",factory.getHeadPoseDelay(),defaults.getHeadPoseDelay());
            JsonUtil.writeNonDefaultInt(out,"viewDistance",factory.getViewDistance(),defaults.getViewDistance());
            JsonUtil.writeNonDefaultFloat(out,"maxRotationStep",factory.getMaxRotationStep(),defaults.getMaxRotationStep());
            JsonUtil.writeNonDefaultFloat(out,"maxRotationStepFlight",factory.getMaxRotationStepFlight(),defaults.getMaxRotationStepFlight());
            JsonUtil.writeNonDefaultInt(out,"updateInterval",factory.getUpdateInterval(),defaults.getUpdateInterval());
            JsonUtil.writeNonDefaultInt(out,"jumpHeight",factory.getJumpHeight(),defaults.getJumpHeight());
            JsonUtil.writeNonDefaultFloat(out,"knockBackBase",factory.getKnockBackBase(),defaults.getKnockBackBase());
            JsonUtil.writeNonDefaultFloat(out,"knockBackPerDamage",factory.getKnockBackPerDamage(),defaults.getKnockBackPerDamage());
            if(!factory.getEnemies().isEmpty()) {
                out.name("enemies").beginArray();
                for(McmeEntity enemy: factory.getEnemies()) {
                    JsonUtil.writeEntityLink(enemy,false,out);
                }
                out.endArray();
            }
        out.endObject();
    }

    @Override
    public VirtualEntityFactory read(JsonReader in) throws IOException {
        return null;
    }
}
