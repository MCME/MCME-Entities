package com.mcmiddleearth.entities.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;

public class LocationAdapter extends TypeAdapter<Location> {

    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        out.beginObject()
                .name("world").value(value.getWorld().getName())
                .name("x").value(value.getX())
                .name("y").value(value.getY())
                .name("z").value(value.getZ())
                .name("yaw").value(value.getYaw())
                .name("pitch").value(value.getPitch())
            .endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonLocation = parser.parse(in).getAsJsonObject();
        World world = jsonLocation.has("world")?Bukkit.getWorld(jsonLocation.get("world").getAsString()):null;
        double x = jsonLocation.has("x")?jsonLocation.get("x").getAsDouble():0;
        double y = jsonLocation.has("y")?jsonLocation.get("y").getAsDouble():0;
        double z = jsonLocation.has("z")?jsonLocation.get("z").getAsDouble():0;
        float yaw = jsonLocation.has("yaw")?jsonLocation.get("yaw").getAsFloat():0;
        float pitch = jsonLocation.has("pitch")?jsonLocation.get("pitch").getAsFloat():0;
        return new Location(world,x,y,z,yaw,pitch);
    }
}
