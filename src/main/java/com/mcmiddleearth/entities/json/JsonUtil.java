package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.Entity;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.attributes.VirtualAttributeFactory;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import org.bukkit.Location;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class JsonUtil {

    private static final String
    REQUIRED        = "required",
    UNIQUE_ID       = "unique_id";

    public static void writeEntityLink(Entity entity, boolean required, JsonWriter out) throws IOException {
        out.beginObject()
                .name(REQUIRED).value(required)
                .name(UNIQUE_ID).value((entity!=null?entity.getUniqueId().toString():null));
        out.endObject();
    }

    public static McmeEntity readEntityLink(JsonReader in) throws IOException {
        boolean required = false;
        UUID uuid = null;
        in.beginObject();
        //try {
            while (in.hasNext()) {
                switch (in.nextName()) {
                    case REQUIRED:
                        required = in.nextBoolean();
                        break;
                    case UNIQUE_ID:
                        String uuidData = in.nextString();
                        if(uuidData != null && !uuidData.equalsIgnoreCase("null")) {
                            uuid = UUID.fromString(uuidData);
                        }
                }
            }
        //} finally {
            in.endObject();
        //}
        if(uuid != null) {
            McmeEntity entity = EntitiesPlugin.getEntityServer().getEntity(uuid);
Logger.getGlobal().info("Found entity: "+entity);
            if(entity != null) {
                return entity;
            } else if(required){
                throw new IllegalArgumentException("Required entity not found!");
            } else {
Logger.getGlobal().info("Creating placeholder!");
                return new Placeholder(uuid);
            }
        } else {
            throw new IllegalArgumentException("Missing UUID for entity.");
        }
    }

    public static void writeNonDefaultAttribute(JsonWriter out, AttributeInstance attributeInstance,
                                                McmeEntityType entityType, Gson gson, boolean writeDefaults) {
        AttributeInstance defaults = VirtualAttributeFactory.getAttributesFor(entityType).get(attributeInstance.getAttribute());
Logger.getGlobal().info("Attribute: "+attributeInstance.getAttribute()+" Defaults: "+defaults);
        if(writeDefaults || (defaults == null || !attributeInstance.getModifiers().isEmpty()
                || attributeInstance.getBaseValue()!=defaults.getBaseValue()
                || attributeInstance.getDefaultValue()!=defaults.getDefaultValue())) {
            gson.toJson(attributeInstance, VirtualEntityAttributeInstance.class,out);
        }
    }

    public static void writeNonDefaultString(JsonWriter out, String name, String value, String defaultValue,
                                             boolean writeDefaults) throws IOException {
        if(writeDefaults || (value != null && !value.equals(defaultValue))) out.name(name).value(value);
    }

    public static void writeNonDefaultUuid(JsonWriter out, String name, UUID value, UUID defaultValue,
                                             boolean writeDefaults) throws IOException {
        if(writeDefaults || (value != null && !value.equals(defaultValue))) {
            out.name(name).value(value.toString());
        }
    }

    public static void writeNonDefaultBoolean(JsonWriter out, String name, boolean value, boolean defaultValue,
                                              boolean writeDefaults) throws IOException {
        if(writeDefaults || value != defaultValue) out.name(name).value(value);
    }

    public static void writeNonDefaultFloat(JsonWriter out, String name, float value, float defaultValue,
                                            boolean writeDefaults) throws IOException {
        if(writeDefaults || value != defaultValue) out.name(name).value(value);
    }

    public static void writeNonDefaultDouble(JsonWriter out, String name, double value, double defaultValue,
                                            boolean writeDefaults) throws IOException {
        if(writeDefaults || value != defaultValue) out.name(name).value(value);
    }

    public static void writeNonDefaultInt(JsonWriter out, String name, int value, int defaultValue,
                                          boolean writeDefaults) throws IOException {
        if(writeDefaults || value != defaultValue) out.name(name).value(value);
    }

    public static void writeNonDefaultVector(JsonWriter out, String name, Vector value, Vector defaultValue, Gson gson,
                                             boolean writeDefaults) throws IOException {
        if(writeDefaults || (value != null && !value.equals(defaultValue))) {
            out.name(name);
            gson.toJson(value, Vector.class, out);
        }
    }

    public static void writeNonDefaultLocation(JsonWriter out, String name, Location value, Location defaultValue, Gson gson,
                                               boolean writeDefaults) throws IOException {
        if(writeDefaults || (value != null && !value.equals(defaultValue))) {
            out.name(name);
            gson.toJson(value, Location.class, out);
        }
    }


}
