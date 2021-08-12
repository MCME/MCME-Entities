package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EntityBoundingBoxAdapter extends TypeAdapter<EntityBoundingBox> {

    @Override
    public void write(JsonWriter out, EntityBoundingBox value) throws IOException {
        out.beginObject();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        out.name("location");
        gson.toJson(value.getLocation(), Vector.class, out);
        out.name("dx");
        gson.toJson(value.getDx(), Integer.class, out);
        out.name("dz");
        gson.toJson(value.getDz(), Integer.class, out);
        out.name("ymin");
        gson.toJson(value.getYMin(), Integer.class, out);
        out.name("ymax");
        gson.toJson(value.getYMax(), Integer.class, out);
        out.endObject();
    }

    @Override
    public EntityBoundingBox read(JsonReader in) throws IOException {
        in.beginObject();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        Map<String,Integer> data = new HashMap<>();
        Vector location = null;
        while(in.hasNext()) {
            String name = in.nextName();
            if(name.equals("location")) {
                location = gson.fromJson(in, Vector.class);
            } else {
                data.put(name, in.nextInt());
            }
        }
        EntityBoundingBox box = new EntityBoundingBox(data.get("dx"),data.get("dz"),data.get("ymin"),data.get("ymax"));
        if(location!=null) {
            box.setLocation(location.toLocation(null));
        }
        return box;
    }
}
