package com.mcmiddleearth.entities.json;

import com.fasterxml.uuid.UUIDGenerator;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import com.mcmiddleearth.entities.util.UuidGenerator;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import javax.swing.table.DefaultTableCellRenderer;
import java.io.IOException;
import java.util.*;

public class AttributeInstanceAdapter extends TypeAdapter<VirtualEntityAttributeInstance> {

    private static final String
    ATTRIBUTE       = "attribute",
    DEFAULT         = "default",
    BASE            = "base",
    MODIFIER        = "modifier",
    NAME            = "name",
    AMOUNT          = "amount",
    OPERATION       = "operation",
    UNIQUE_ID       = "unique_id",
    SLOT            = "slot";

    @Override
    public void write(JsonWriter out, VirtualEntityAttributeInstance value) throws IOException {
        out.beginObject();
            out.name(ATTRIBUTE).value(value.getAttribute().name().toLowerCase())
               .name(DEFAULT).value(value.getDefaultValue())
               .name(BASE).value(value.getBaseValue());
            Collection<AttributeModifier> modifiers = value.getModifiers();
            if(!modifiers.isEmpty()) {
                out.name(MODIFIER);
                out.beginArray();
                    for(AttributeModifier modifier: modifiers) {
                        out.beginObject();
                            out.name(NAME).value(modifier.getName())
                               .name(AMOUNT).value(modifier.getAmount())
                               .name(OPERATION).value(modifier.getOperation().name().toLowerCase())
                               .name(UNIQUE_ID).value(modifier.getUniqueId().toString());
                            if(modifier.getSlot()!=null) {
                                out.name(SLOT).value(modifier.getSlot().name().toLowerCase());
                            }
                        out.endObject();
                    }
                out.endArray();
            }
        out.endObject();
    }

    @Override
    public VirtualEntityAttributeInstance read(JsonReader in) throws IOException {
        VirtualEntityAttributeInstance instance = new VirtualEntityAttributeInstance(Attribute.GENERIC_MAX_HEALTH,20);
        in.beginObject();
        //try {
            while (in.hasNext()) {
                switch(in.nextName()) {
                    case ATTRIBUTE:
                        instance.setAttribute(Attribute.valueOf(in.nextString().toUpperCase()));
                        break;
                    case DEFAULT:
                        instance.setDefaultValue(in.nextDouble());
                        break;
                    case BASE:
                        instance.setBaseValue(in.nextDouble());
                        break;
                    case MODIFIER:
                        List<AttributeModifier> modifiers = new ArrayList<>();
                        in.beginArray();
                        //try {
                            while(in.hasNext()) {
                                in.beginObject();
                                //try {
                                    String name = "";
                                    AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
                                    double amount = 0;
                                    UUID uniqueId = UuidGenerator.fast_random();
                                    EquipmentSlot slot = null;
                                    while(in.hasNext()) {
                                        String key = in.nextName();
                                        switch(key) {
                                            case NAME:
                                                name = in.nextString();
                                                break;
                                            case AMOUNT:
                                                amount = in.nextDouble();
                                                break;
                                            case OPERATION:
                                                operation = AttributeModifier.Operation.valueOf(in.nextString().toUpperCase());
                                                break;
                                            case UNIQUE_ID:
                                                uniqueId = UUID.fromString(in.nextString());
                                                break;
                                            case SLOT:
                                                slot = EquipmentSlot.valueOf(in.nextString().toUpperCase());
                                                break;
                                            default:
                                                in.skipValue();
                                        }
                                    }
                                    modifiers.add(new AttributeModifier(uniqueId, name, amount, operation, slot));
                                //} finally {
                                in.endObject(); //}
                            }
                        //} finally {
                        in.endArray(); //}
                        instance.setModifiers(modifiers);
                        break;
                    default:
                        in.skipValue();
                }
            }
        //} finally {
                in.endObject();
            //}
            return instance;
    }
}
