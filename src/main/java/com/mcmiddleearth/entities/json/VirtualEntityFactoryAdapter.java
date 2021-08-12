package com.mcmiddleearth.entities.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;

import java.io.IOException;

public class VirtualEntityFactoryAdapter extends TypeAdapter<VirtualEntityFactory> {

    @Override
    public void write(JsonWriter out, VirtualEntityFactory value) throws IOException {

    }

    @Override
    public VirtualEntityFactory read(JsonReader in) throws IOException {
        return null;
    }
}
