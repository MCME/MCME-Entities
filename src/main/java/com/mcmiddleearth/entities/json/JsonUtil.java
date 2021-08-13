package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.api.Entity;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.entities.attributes.VirtualAttributeFactory;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import org.bukkit.Location;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.util.Vector;

import java.io.IOException;

public class JsonUtil {

    public static void writeEntityLink(Entity entity, boolean required, JsonWriter out) throws IOException {
        out.beginObject()
                .name("required").value(required)
                .name("uniqueid").value(entity.getUniqueId().toString());
        out.endObject();
    }

    public static void writeNonDefaultAttribute(JsonWriter out, AttributeInstance attributeInstance, McmeEntityType entityType, Gson gson) {
        AttributeInstance defaults = VirtualAttributeFactory.getAttributesFor(entityType).get(attributeInstance.getAttribute());
        if(!attributeInstance.getModifiers().isEmpty() || attributeInstance.getBaseValue()!=defaults.getBaseValue()
                || attributeInstance.getDefaultValue()!=defaults.getDefaultValue()) {
            gson.toJson(attributeInstance, VirtualEntityAttributeInstance.class,out);
        }
    }

    public static void writeNonDefaultString(JsonWriter out, String name, String value, String defaultValue) throws IOException {
        if(value != null && !value.equals(defaultValue)) out.name(name).value(value);
    }

    public static void writeNonDefaultBoolean(JsonWriter out, String name, boolean value, boolean defaultValue) throws IOException {
        if(value != defaultValue) out.name(name).value(value);
    }

    public static void writeNonDefaultFloat(JsonWriter out, String name, float value, float defaultValue) throws IOException {
        if(value != defaultValue) out.name(name).value(value);
    }

    public static void writeNonDefaultInt(JsonWriter out, String name, int value, int defaultValue) throws IOException {
        if(value != defaultValue) out.name(name).value(value);
    }

    public static void writeNonDefaultVector(JsonWriter out, String name, Vector value, Vector defaultValue, Gson gson) throws IOException {
        if(value != null && !value.equals(defaultValue)) {
            out.name(name);
            gson.toJson(value, Vector.class, out);
        }
    }

    public static void writeNonDefaultLocation(JsonWriter out, String name, Location value, Location defaultValue, Gson gson) throws IOException {
        if(value != null && !value.equals(defaultValue)) {
            out.name(name);
            gson.toJson(value, Location.class, out);
        }
    }


}
