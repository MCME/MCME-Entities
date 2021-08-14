package com.mcmiddleearth.entities.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.IOException;

public class VectorAdapter extends TypeAdapter<Vector> {

    @Override
    public void write(JsonWriter out, Vector value) throws IOException {
        out.beginObject()
                .name("x").value(value.getX())
                .name("y").value(value.getY())
                .name("z").value(value.getZ())
           .endObject();
    }

    @Override
    public Vector read(JsonReader in) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonLocation = parser.parse(in).getAsJsonObject();
        double x = jsonLocation.has("x")?jsonLocation.get("x").getAsDouble():0;
        double y = jsonLocation.has("y")?jsonLocation.get("y").getAsDouble():0;
        double z = jsonLocation.has("z")?jsonLocation.get("z").getAsDouble():0;
        return new Vector(x,y,z);
    }
}
