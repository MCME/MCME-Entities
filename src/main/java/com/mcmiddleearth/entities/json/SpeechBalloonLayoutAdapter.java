package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import org.bukkit.util.Vector;

import java.io.IOException;

public class SpeechBalloonLayoutAdapter extends TypeAdapter<SpeechBalloonLayout> {

    @Override
    public void write(JsonWriter out, SpeechBalloonLayout balloon) throws IOException {
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        out.beginObject();
        out.name("balloonVisible").value(balloon.hasBalloon())
           .name("balloonMaterial").value(balloon.getBalloonMaterial().name().toLowerCase())
           .name("linePrefix").value(balloon.getLinePrefix())
           .name("linePitch").value(balloon.getLinePitch())
           .name("width").value(balloon.getWidth().name().toLowerCase())
           .name("lineLengthNarrow").value(balloon.getLineLengthNarrow())
           .name("lineLengthWide").value(balloon.getLineLengthWide())
           .name("widthInBlocksNarrow").value(balloon.getWidthInBlocksNarrow())
           .name("widthInBlocksWide").value(balloon.getWidthInBlocksWide())
           .name("position").value(balloon.getPosition().name().toLowerCase());
        gson.toJson(balloon.getBaseOffset(),Vector.class,out);
        gson.toJson(balloon.getLayoutOffset(),Vector.class,out);
        out.name("modelDataNarrow").beginArray();
        for(Integer data: balloon.getModelDataNarrow()) {
            out.value(data);
        }
        out.endArray();
        out.name("modelDataWide").beginArray();
        for(Integer data: balloon.getModelDataWide()) {
            out.value(data);
        }
        out.endArray();

    }

    @Override
    public SpeechBalloonLayout read(JsonReader in) throws IOException {
        return null;
    }


}
