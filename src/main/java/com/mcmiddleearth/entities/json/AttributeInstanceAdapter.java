package com.mcmiddleearth.entities.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

import java.io.IOException;
import java.util.Collection;

public class AttributeInstanceAdapter extends TypeAdapter<VirtualEntityAttributeInstance> {

    @Override
    public void write(JsonWriter out, VirtualEntityAttributeInstance value) throws IOException {
        out.beginObject();
            out.name("attribute").value(value.getAttribute().name().toLowerCase())
               .name("default").value(value.getDefaultValue())
               .name("base").value(value.getBaseValue());
            Collection<AttributeModifier> modifiers = value.getModifiers();
            if(!modifiers.isEmpty()) {
                out.name("modifier");
                out.beginArray();
                    for(AttributeModifier modifier: modifiers) {
                        out.beginObject();
                            out.name("name").value(modifier.getName())
                               .name("amount").value(modifier.getAmount())
                               .name("operation").value(modifier.getOperation().name().toLowerCase())
                               .name("uniqueId").value(modifier.getUniqueId().toString());
                            if(modifier.getSlot()!=null) {
                                out.name("slot").value(modifier.getSlot().name().toLowerCase());
                            }
                        out.endObject();
                    }
                out.endArray();
            }
        out.endObject();
    }

    @Override
    public VirtualEntityAttributeInstance read(JsonReader in) throws IOException {
        return null;
    }
}
