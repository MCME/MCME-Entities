package com.mcmiddleearth.entities.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.GoalType;
import com.mcmiddleearth.entities.ai.goal.head.*;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

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
        VirtualEntityGoalFactory factory = VirtualEntityGoalFactory.getDefaults();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        in.beginObject();
        while(in.hasNext()) {
            String key = in.nextName();
            try {
                switch(key) {
                    case "goalType":
                        factory.withGoalType(GoalType.valueOf(in.nextString()));
                        break;
                    case "movementSpeed":
                        factory.withMovementSpeed(MovementSpeed.valueOf(in.nextString()));
                        break;
                    case "targetLocation":
                        factory.withTargetLocation(gson.fromJson(in,Location.class));
                        break;
                    case "checkpoints":
                        List<Location> checkpoints = new ArrayList<>();
                        in.beginArray();
                        try {
                            while(in.hasNext()) {
                                checkpoints.add(gson.fromJson(in,Location.class));
                            }
                        } finally { in.endArray(); }
                        factory.withCheckpoints(checkpoints.toArray(new Location[0]));
                        break;
                    case "startCheckPoint":
                        factory.withStartCheckpoint(in.nextInt());
                        break;
                    case "targetEntity":
                        factory.withTargetEntity(JsonUtil.readEntityLink(in));
                        break;
                    case "updateInterval":
                        factory.withUpdateInterval(in.nextInt());
                        break;
                    case "loop":
                        factory.withLoop(in.nextBoolean());
                        break;
                    case "headGoals":
                        Set<HeadGoal>headGoals = new HashSet<>();
                        in.beginArray();
                        try {
                            while(in.hasNext()) {
                                in.beginObject();
                                try {
                                    HeadGoalType type = null;
                                    int duration = 15;
                                    McmeEntity targetEntity = null;
                                    Location targetLocation = null;
                                    float yaw = 0, pitch = 0;
                                    while(in.hasNext()) {
                                        switch(in.nextName()) {
                                            case "type":
                                                type = HeadGoalType.valueOf(in.nextString());
                                                break;
                                            case "targetLocation":
                                                targetLocation = gson.fromJson(in,Location.class);
                                                break;
                                            case "targetEntity":
                                                targetEntity = JsonUtil.readEntityLink(in);
                                                break;
                                            case "yaw":
                                                yaw = (float) in.nextDouble();
                                                break;
                                            case "pitch":
                                                pitch = (float) in.nextDouble();
                                                break;
                                            case "duration":
                                                duration = in.nextInt();
                                                break;
                                            default:
                                                in.skipValue();
                                        }
                                    }
                                    if(type != null) {
                                        switch (type) {
                                            case ENTITY_TARGET:
                                                headGoals.add(new HeadGoalEntityTarget(null,duration));
                                                break;
                                            case LOCATION_TARGET:
                                                headGoals.add(new HeadGoalLocationTarget(null,duration));
                                                break;
                                            case LOOK:
                                                headGoals.add(new HeadGoalLook(targetLocation, null,duration));
                                                break;
                                            case STARE:
                                                headGoals.add(new HeadGoalStare(yaw,pitch,duration));
                                                break;
                                            case WATCH:
                                                headGoals.add(new HeadGoalWatch(targetEntity, null,duration));
                                                break;
                                            case WAYPOINT_TARGET:
                                                headGoals.add(new HeadGoalWaypointTarget(null,duration));
                                                break;
                                        }
                                    }
                                } finally { in.endObject(); }
                            }
                        } finally { in.endArray(); }
                        factory.withHeadGoals(headGoals);
                        break;
                    default:
                        in.skipValue();
                }
            } catch (IllegalArgumentException | IllegalStateException | JsonSyntaxException ex) {
                Logger.getLogger(VirtualEntityGoalFactoryAdapter.class.getSimpleName()).warning("Error reading key: "+key+" -> "+ex.getMessage());
            }
        }
        in.endObject();
        return null;
    }
}
