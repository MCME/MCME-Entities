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

    private static final String
    X   = "x",
    Y   = "y",
    Z   = "z";

    @Override
    public void write(JsonWriter out, Vector value) throws IOException {
        out.beginObject()
                .name(X).value(value.getX())
                .name(Y).value(value.getY())
                .name(Z).value(value.getZ())
           .endObject();
    }

    @Override
    public Vector read(JsonReader in) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonLocation = parser.parse(in).getAsJsonObject();
        double x = jsonLocation.has(X)?jsonLocation.get(X).getAsDouble():0;
        double y = jsonLocation.has(Y)?jsonLocation.get(Y).getAsDouble():0;
        double z = jsonLocation.has(Z)?jsonLocation.get(Z).getAsDouble():0;
        return new Vector(x,y,z);
    }
}
