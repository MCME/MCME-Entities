package com.mcmiddleearth.entities.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;

import java.io.IOException;

public class VirtualEntityGoalFactoryAdapter extends TypeAdapter<VirtualEntityGoalFactory> {

    @Override
    public void write(JsonWriter out, VirtualEntityGoalFactory value) throws IOException {

    }

    @Override
    public VirtualEntityGoalFactory read(JsonReader in) throws IOException {
        return null;
    }
}
