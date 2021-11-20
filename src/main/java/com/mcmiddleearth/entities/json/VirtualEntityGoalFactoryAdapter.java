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
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VirtualEntityGoalFactoryAdapter extends TypeAdapter<VirtualEntityGoalFactory> {

    private static final String
    GOAL_TYPE               = "goal_type",
    MOVEMENT_SPEED          = "movement_speed",
    TARGET_LOCATION         = "target_location",
    CHECKPOINTS             = "checkpoint",
    START_CHECKPOINT        = "start_checkpoint",
    TARGET_ENTITY           = "target_entity",
    UPDATE_INTERVAL         = "update_interval",
    LOOP                    = "loop",
    HEAD_GOALS              = "head_goals",
    DURATION                = "duration",
    TYPE                    = "type",
    YAW                     = "yaw",
    PITCH                   = "pitch",
    RELATIVE_POSITION       = "relative_position",
    FLIGHT_LEVEL            = "flight_level",
    ATTACK_PITCH            = "attack_pitch",
    DIVE                    = "dive";

    @Override
    public void write(JsonWriter out, VirtualEntityGoalFactory factory) throws IOException {
        boolean writeDefaults = factory.isWriteDefaultsToFile();
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        VirtualEntityGoalFactory defaults = VirtualEntityGoalFactory.getDefaults();
        out.beginObject();
            JsonUtil.writeNonDefaultString(out,GOAL_TYPE,factory.getGoalType().name().toLowerCase(),
                                                                defaults.getGoalType().name().toLowerCase(), writeDefaults);
            JsonUtil.writeNonDefaultString(out,MOVEMENT_SPEED,factory.getMovementSpeed().name().toLowerCase(),
                                                                 defaults.getMovementSpeed().name().toLowerCase(),writeDefaults);
            JsonUtil.writeNonDefaultLocation(out,TARGET_LOCATION,factory.getTargetLocation(),
                                                                defaults.getTargetLocation(), gson, writeDefaults);
            if(writeDefaults || (factory.getCheckpoints()!=null && factory.getCheckpoints().length>0)) {
                out.name(CHECKPOINTS).beginArray();
                if(factory.getCheckpoints()!=null) {
                    for (Location checkpoint : factory.getCheckpoints()) {
                        gson.toJson(checkpoint, Location.class, out);
                    }
                }
                out.endArray();
            }
            JsonUtil.writeNonDefaultInt(out,START_CHECKPOINT,factory.getStartCheckpoint(),
                                            defaults.getStartCheckpoint(), writeDefaults);
            if(writeDefaults || factory.getTargetEntity() != null) {
                out.name(TARGET_ENTITY);
                JsonUtil.writeEntityLink(factory.getTargetEntity(), false, out);
            }
            JsonUtil.writeNonDefaultInt(out,UPDATE_INTERVAL,factory.getUpdateInterval(),
                                            defaults.getUpdateInterval(),writeDefaults);
            JsonUtil.writeNonDefaultBoolean(out, LOOP, factory.isLoop(), defaults.isLoop(),writeDefaults);
            if(writeDefaults || (factory.getHeadGoals()!=null && !factory.getHeadGoals().isEmpty())) {
               out.name(HEAD_GOALS).beginArray();
               if(factory.getHeadGoals()!=null) {
                   for (HeadGoal headGoal : factory.getHeadGoals()) {
                       out.beginObject();
                       out.name(DURATION).value(headGoal.getDuration());
                       if(headGoal.getClass().equals(HeadGoalEntityTarget.class)) {
                           out.name(TYPE).value(HeadGoalType.ENTITY_TARGET.name().toLowerCase());
                       } else if(headGoal.getClass().equals(HeadGoalLocationTarget.class)) {
                               out.name(TYPE).value(HeadGoalType.LOCATION_TARGET.name().toLowerCase());
                       } else if(headGoal.getClass().equals(HeadGoalLook.class)) {
                               out.name(TYPE).value(HeadGoalType.LOOK.name().toLowerCase());
                               out.name(TARGET_LOCATION);
                               gson.toJson(((HeadGoalLook) headGoal).getTarget(), Location.class, out);
                       } else if(headGoal.getClass().equals(HeadGoalStare.class)) {
                               out.name(TYPE).value(HeadGoalType.STARE.name().toLowerCase());
                               out.name(YAW).value(headGoal.getHeadYaw())
                                       .name(PITCH).value(headGoal.getHeadPitch());
                       } else if(headGoal.getClass().equals(HeadGoalWatch.class)) {
                               out.name(TYPE).value(HeadGoalType.WATCH.name().toLowerCase());
                               out.name(TARGET_ENTITY);
                               JsonUtil.writeEntityLink(((HeadGoalWatch) headGoal).getTarget(), false, out);
                       } else if(headGoal.getClass().equals(HeadGoalWaypointTarget.class)) {
                           out.name(TYPE).value(HeadGoalType.WAYPOINT_TARGET.name().toLowerCase());
                       } else {
                               out.name(TYPE).value(headGoal.getClass().getSimpleName());
                       }
                       out.endObject();
                   }
               }
               out.endArray();
               JsonUtil.writeNonDefaultVector(out,RELATIVE_POSITION,factory.getRelativePosition(),
                                              defaults.getRelativePosition(),gson,writeDefaults);
               JsonUtil.writeNonDefaultDouble(out,FLIGHT_LEVEL,factory.getFlightLevel(),20,writeDefaults);
               JsonUtil.writeNonDefaultFloat(out,ATTACK_PITCH,factory.getAttackPitch(),45,writeDefaults);
               JsonUtil.writeNonDefaultDouble(out,DIVE,factory.getDive(),1.15,writeDefaults);
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
                    case GOAL_TYPE:
                        factory.withGoalType(GoalType.valueOf(in.nextString().toUpperCase()));
                        break;
                    case MOVEMENT_SPEED:
                        factory.withMovementSpeed(MovementSpeed.valueOf(in.nextString().toUpperCase()));
                        break;
                    case TARGET_LOCATION:
                        factory.withTargetLocation(gson.fromJson(in,Location.class));
                        break;
                    case CHECKPOINTS:
                        List<Location> checkpoints = new ArrayList<>();
                        in.beginArray();
                        try {
                            while(in.hasNext()) {
                                checkpoints.add(gson.fromJson(in,Location.class));
                            }
                        } finally { in.endArray(); }
                        factory.withCheckpoints(checkpoints.toArray(new Location[0]));
                        break;
                    case START_CHECKPOINT:
                        factory.withStartCheckpoint(in.nextInt());
                        break;
                    case TARGET_ENTITY:
                        factory.withTargetEntity(JsonUtil.readEntityLink(in));
                        break;
                    case UPDATE_INTERVAL:
                        factory.withUpdateInterval(in.nextInt());
                        break;
                    case LOOP:
                        factory.withLoop(in.nextBoolean());
                        break;
                    case RELATIVE_POSITION:
                        factory.withRelativePosition(gson.fromJson(in, Vector.class));
                        break;
                    case FLIGHT_LEVEL:
                        factory.withFlightLevel(in.nextDouble());
                        break;
                    case ATTACK_PITCH:
                        factory.withAttackPitch((float)in.nextDouble());
                        break;
                    case DIVE:
                        factory.withDive(in.nextDouble());
                        break;
                    case HEAD_GOALS:
                        Set<HeadGoal>headGoals = new HashSet<>();
                        in.beginArray();
                        //try {
                            while(in.hasNext()) {
                                in.beginObject();
                                //try {
                                    HeadGoalType type = null;
                                    int duration = 15;
                                    McmeEntity targetEntity = null;
                                    Location targetLocation = null;
                                    float yaw = 0, pitch = 0;
                                    while(in.hasNext()) {
                                        switch(in.nextName()) {
                                            case TYPE:
                                                type = HeadGoalType.valueOf(in.nextString().toUpperCase());
                                                break;
                                            case TARGET_LOCATION:
                                                targetLocation = gson.fromJson(in,Location.class);
                                                break;
                                            case TARGET_ENTITY:
                                                targetEntity = JsonUtil.readEntityLink(in);
                                                break;
                                            case YAW:
                                                yaw = (float) in.nextDouble();
                                                break;
                                            case PITCH:
                                                pitch = (float) in.nextDouble();
                                                break;
                                            case DURATION:
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
                                //} finally {
                            in.endObject(); //}
                            }
                        //} finally {
                        in.endArray(); //}
                        factory.withHeadGoals(headGoals);
                        break;
                    default:
                        in.skipValue();
                }
            } catch (IllegalArgumentException | IllegalStateException | JsonSyntaxException ex) {
                //Logger.getLogger(VirtualEntityGoalFactoryAdapter.class.getSimpleName()).warning("Error reading key: "+key+" -> "+ex.getMessage());
                throw new IllegalArgumentException("Error reading key: "+key+" at "+in.getPath() + " -> "+ex.getMessage());
            }
        }
        in.endObject();
        return factory;
    }
}
