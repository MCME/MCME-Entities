package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloon;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
        out.name("baseOffset");
        gson.toJson(balloon.getBaseOffset(),Vector.class,out);
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
        SpeechBalloonLayout layout = new SpeechBalloonLayout();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        in.beginObject();
        while(in.hasNext()) {
            String key = in.nextName();
            try {
                switch(key) {
                    case "balloonVisible":
                        layout.withBalloon(in.nextBoolean());
                        break;
                    case "balloonMaterial":
                        layout.withBalloonMaterial(Material.valueOf(in.nextString()));
                        break;
                    case "linePrefix":
                        layout.withLinePrefix(in.nextString());
                        break;
                    case "linePitch":
                        layout.withLinePitch(in.nextInt());
                        break;
                    case "width":
                        layout.withWidth(SpeechBalloonLayout.Width.valueOf(in.nextString()));
                        break;
                    case "lineLengthNarrow":
                        layout.withLineLengthNarrow(in.nextInt());
                        break;
                    case "lineLengthWide":
                        layout.withLineLengthWide(in.nextInt());
                        break;
                    case "widthInBlocksNarrow":
                        layout.withWidthInBlocksNarrow(in.nextDouble());
                        break;
                    case "widthInBlocksWide":
                        layout.withWidthInBlocksWide(in.nextDouble());
                        break;
                    case "position":
                        layout.withPosition(SpeechBalloonLayout.Position.valueOf(in.nextString()));
                        break;
                    case "baseOffset":
                        layout.withBaseOffset(gson.fromJson(in,Vector.class));
                        break;
                    case "modelDataNarrow":
                        List<Integer> data = new ArrayList<>();
                        in.beginArray();
                        try {
                            while(in.hasNext()) {
                                data.add(in.nextInt());
                            }
                        } finally { in.endArray(); }
                        layout.withBalloonModelData(data.toArray(new Integer[0]),false);
                        break;
                    case "modelDataWide":
                        data = new ArrayList<>();
                        in.beginArray();
                        try {
                            while(in.hasNext()) {
                                data.add(in.nextInt());
                            }
                        } finally { in.endArray(); }
                        layout.withBalloonModelData(data.toArray(new Integer[0]),true);
                        break;
                    default:
                        in.skipValue();
                }
            } catch (IllegalArgumentException | IllegalStateException | JsonSyntaxException ex) {
                Logger.getLogger(SpeechBalloonLayoutAdapter.class.getSimpleName()).warning("Error reading key: "+key+" -> "+ex.getMessage());
            }
        }
        in.endObject();
        return layout;
    }


}
