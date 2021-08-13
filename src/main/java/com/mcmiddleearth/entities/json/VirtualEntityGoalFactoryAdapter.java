package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.GoalType;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoal;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalLook;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalType;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoalWatch;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class VirtualEntityGoalFactoryAdapter extends TypeAdapter<VirtualEntityGoalFactory> {

    @Override
    public void write(JsonWriter out, VirtualEntityGoalFactory factory) throws IOException {
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        VirtualEntityGoalFactory defaults = VirtualEntityGoalFactory.getDefaults();
        out.beginObject();
            JsonUtil.writeNonDefaultString(out,"goalType",factory.getGoalType().name().toLowerCase(),
                                                                defaults.getGoalType().name().toLowerCase());
            JsonUtil.writeNonDefaultString(out,"movementSpeed",factory.getMovementSpeed().name().toLowerCase(),
                                                                     defaults.getMovementSpeed().name().toLowerCase());
            JsonUtil.writeNonDefaultLocation(out,"targetLocation",factory.getTargetLocation(),defaults.getTargetLocation(), gson);
            if(factory.getCheckpoints()!=null && factory.getCheckpoints().length>0) {
                out.name("checkpoints").beginArray();
                for(Location checkpoint: factory.getCheckpoints()) {
                    gson.toJson(checkpoint,Location.class,out);
                }
                out.endArray();
            }
            JsonUtil.writeNonDefaultInt(out,"startCheckPoint",factory.getStartCheckpoint(),defaults.getStartCheckpoint());
            if(factory.getTargetEntity() != null) {
                out.name("targetEntity");
                JsonUtil.writeEntityLink(factory.getTargetEntity(), false, out);
            }
            JsonUtil.writeNonDefaultInt(out,"updateInterval",factory.getUpdateInterval(),defaults.getUpdateInterval());
            JsonUtil.writeNonDefaultBoolean(out, "loop", factory.isLoop(), defaults.isLoop());
            if(factory.getHeadGoals()!=null && !factory.getHeadGoals().isEmpty()) {
               out.name("headGoals").beginArray();
               for(HeadGoal headGoal: factory.getHeadGoals()) {
                   out.beginObject();
                   out.name("duration").value(headGoal.getDuration());
                   switch(headGoal.getClass().getSimpleName()) {
                       case "HeadGoalEntityTarget":
                           out.name("type").value(HeadGoalType.ENTITY_TARGET.name().toLowerCase());
                           break;
                       case "HeadGoalLocationTarget":
                           out.name("type").value(HeadGoalType.LOCATION_TARGET.name().toLowerCase());
                           break;
                       case "HeadGoalLook":
                           out.name("type").value(HeadGoalType.LOOK.name().toLowerCase());
                           out.name("targetLocation");
                           gson.toJson(((HeadGoalLook)headGoal).getTarget(),Location.class,out);
                           break;
                       case "HeadGoalStare":
                           out.name("type").value(HeadGoalType.STARE.name().toLowerCase());
                           out.name("yaw").value(headGoal.getHeadYaw())
                              .name("pitch").value(headGoal.getHeadPitch());
                           break;
                       case "HeadGoalWatch":
                           out.name("type").value(HeadGoalType.WATCH.name().toLowerCase());
                           out.name("targetEntity");
                           JsonUtil.writeEntityLink(((HeadGoalWatch)headGoal).getTarget(),false,out);
                           break;
                       case "HeadGoalWaypointTarget":
                           out.name("type").value(HeadGoalType.WAYPOINT_TARGET.name().toLowerCase());
                           break;
                       default:
                           out.name("type").value(headGoal.getClass().getSimpleName());
                   }
                   out.endObject();
               }
               out.endArray();
            }
        out.endObject();
    }

    @Override
    public VirtualEntityGoalFactory read(JsonReader in) throws IOException {
        return null;
    }
}