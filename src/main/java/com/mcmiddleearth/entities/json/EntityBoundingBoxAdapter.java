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

    private static final String
    LOCATION        = "location",
    DX              = "dx",
    DZ              = "dz",
    Y_MIN           = "y_min",
    Y_MAX           = "y_max";

    @Override
    public void write(JsonWriter out, EntityBoundingBox value) throws IOException {
        out.beginObject();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        out.name(LOCATION);
        gson.toJson(value.getLocation(), Vector.class, out);
        out.name(DX);
        gson.toJson(value.getDx(), Double.class, out);
        out.name(DZ);
        gson.toJson(value.getDz(), Double.class, out);
        out.name(Y_MIN);
        gson.toJson(value.getYMin(), Double.class, out);
        out.name(Y_MAX);
        gson.toJson(value.getYMax(), Double.class, out);
        out.endObject();
    }

    @Override
    public EntityBoundingBox read(JsonReader in) throws IOException {
        EntityBoundingBox box;
        in.beginObject();
        //try {
            Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
            Map<String, Double> data = new HashMap<>();
            Vector location = null;
            while (in.hasNext()) {
                String name = in.nextName();
                if (name.equals(LOCATION)) {
                    location = gson.fromJson(in, Vector.class);
                } else {
                    data.put(name, in.nextDouble());
                }
            }
            box = new EntityBoundingBox(data.get(DX),data.get(DZ),data.get(Y_MIN),data.get(Y_MAX));
            if(location!=null) {
                box.setLocation(location.toLocation(null));
            }
        //} finally {
            in.endObject();
        //}
        return box;
    }
}
