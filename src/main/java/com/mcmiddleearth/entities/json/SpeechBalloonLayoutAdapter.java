package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpeechBalloonLayoutAdapter extends TypeAdapter<SpeechBalloonLayout> {

    private static final String
    BALLOON_VISIBLE         = "balloon_visible",
    BALLOON_MATERIAL        = "balloon_material",
    LINE_PREFIX             = "line_prefix",
    LINE_PITCH              = "line_pitch",
    WIDTH                   = "width",
    LINE_LENGTH_NARROW      = "line_length_narrow",
    LINE_LENGTH_WIDE        = "line_length_wide",
    WIDTH_IN_BLOCKS_NARROW  = "width_in_blocks_narrow",
    WIDTH_IN_BLOCKS_WIDE    = "width_in_blocks_wide",
    POSITION                = "position",
    BASE_OFFSET             = "base_offset",
    MODEL_DATA_NARROW       = "model_data_narrow",
    MODEL_DATA_WIDE         = "model_data_wide";

    @Override
    public void write(JsonWriter out, SpeechBalloonLayout balloon) throws IOException {
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        out.beginObject();
        out.name(BALLOON_VISIBLE).value(balloon.hasBalloon())
           .name(BALLOON_MATERIAL).value(balloon.getBalloonMaterial().name().toLowerCase())
           .name(LINE_PREFIX).value(balloon.getLinePrefix())
           .name(LINE_PITCH).value(balloon.getLinePitch())
           .name(WIDTH).value(balloon.getWidth().name().toLowerCase())
           .name(LINE_LENGTH_NARROW).value(balloon.getLineLengthNarrow())
           .name(LINE_LENGTH_WIDE).value(balloon.getLineLengthWide())
           .name(WIDTH_IN_BLOCKS_NARROW).value(balloon.getWidthInBlocksNarrow())
           .name(WIDTH_IN_BLOCKS_WIDE).value(balloon.getWidthInBlocksWide())
           .name(POSITION).value(balloon.getPosition().name().toLowerCase());
        out.name(BASE_OFFSET);
        gson.toJson(balloon.getBaseOffset(),Vector.class,out);
        out.name(MODEL_DATA_NARROW).beginArray();
        for(Integer data: balloon.getModelDataNarrow()) {
            out.value(data);
        }
        out.endArray();
        out.name(MODEL_DATA_WIDE).beginArray();
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
                    case BALLOON_VISIBLE:
                        layout.withBalloon(in.nextBoolean());
                        break;
                    case BALLOON_MATERIAL:
                        layout.withBalloonMaterial(Material.valueOf(in.nextString().toUpperCase()));
                        break;
                    case LINE_PREFIX:
                        layout.withLinePrefix(in.nextString());
                        break;
                    case LINE_PITCH:
                        layout.withLinePitch(in.nextInt());
                        break;
                    case WIDTH:
                        layout.withWidth(SpeechBalloonLayout.Width.valueOf(in.nextString().toUpperCase()));
                        break;
                    case LINE_LENGTH_NARROW:
                        layout.withLineLengthNarrow(in.nextInt());
                        break;
                    case LINE_LENGTH_WIDE:
                        layout.withLineLengthWide(in.nextInt());
                        break;
                    case WIDTH_IN_BLOCKS_NARROW:
                        layout.withWidthInBlocksNarrow(in.nextDouble());
                        break;
                    case WIDTH_IN_BLOCKS_WIDE:
                        layout.withWidthInBlocksWide(in.nextDouble());
                        break;
                    case POSITION:
                        layout.withPosition(SpeechBalloonLayout.Position.valueOf(in.nextString().toUpperCase()));
                        break;
                    case BASE_OFFSET:
                        layout.withBaseOffset(gson.fromJson(in,Vector.class));
                        break;
                    case MODEL_DATA_NARROW:
                        List<Integer> data = new ArrayList<>();
                        in.beginArray();
                        //try {
                            while(in.hasNext()) {
                                data.add(in.nextInt());
                            }
                        //} finally {
                        in.endArray(); //}
                        layout.withBalloonModelData(data.toArray(new Integer[0]),false);
                        break;
                    case MODEL_DATA_WIDE:
                        data = new ArrayList<>();
                        in.beginArray();
                        //try {
                            while(in.hasNext()) {
                                data.add(in.nextInt());
                            }
                        //} finally {
                        in.endArray(); //}
                        layout.withBalloonModelData(data.toArray(new Integer[0]),true);
                        break;
                    default:
                        in.skipValue();
                }
            } catch (IllegalArgumentException | IllegalStateException | JsonSyntaxException ex) {
                //Logger.getLogger(SpeechBalloonLayoutAdapter.class.getSimpleName()).warning("Error reading key: "+key+" -> "+ex.getMessage());
                throw new IllegalArgumentException("Error reading key: "+key+" at "+in.getPath() + " -> "+ex.getMessage());
            }
        }
        in.endObject();
        return layout;
    }


}
