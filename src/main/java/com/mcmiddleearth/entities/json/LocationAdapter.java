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

    private static final String
    WORLD       = "world",
    X           = "x",
    Y           = "y",
    Z           = "z",
    YAW         = "yaw",
    PITCH       = "pitch";

    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        out.beginObject()
                .name(WORLD).value(value.getWorld().getName())
                .name(X).value(value.getX())
                .name(Y).value(value.getY())
                .name(Z).value(value.getZ())
                .name(YAW).value(value.getYaw())
                .name(PITCH).value(value.getPitch())
            .endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonLocation = parser.parse(in).getAsJsonObject();
        World world = jsonLocation.has(WORLD)?Bukkit.getWorld(jsonLocation.get(WORLD).getAsString()):null;
        double x = jsonLocation.has(X)?jsonLocation.get(X).getAsDouble():0;
        double y = jsonLocation.has(Y)?jsonLocation.get(Y).getAsDouble():0;
        double z = jsonLocation.has(Z)?jsonLocation.get(Z).getAsDouble():0;
        float yaw = jsonLocation.has(YAW)?jsonLocation.get(YAW).getAsFloat():0;
        float pitch = jsonLocation.has(PITCH)?jsonLocation.get(PITCH).getAsFloat():0;
        return new Location(world,x,y,z,yaw,pitch);
    }
}
