package com.mcmiddleearth.entities.entities.composite.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation {

    private final List<Frame> frames = new ArrayList<>();

    private int currentFrame, ticks;

    private final AnimationType type;

    private final String next;

    private final int interval;

    private boolean finished;

    private final BakedAnimationEntity entity;

    public Animation(BakedAnimationEntity entity, AnimationType type, String next, int interval) {
        this.entity = entity;
        this.type = type;
        this.next = next;
        this.interval = interval;
        /*for (int i = 0; i < states.length; i++) {
            this.states.put(states[i], i);
        }*/
        reset();
    }

    public void reset() {
        currentFrame = -1;
        ticks = -1;
        finished = false;
    }

    public void doTick() {
        if(finished) {
            return;
        }
        ticks++;
        if(ticks%interval==0) {
            currentFrame++;
            if(currentFrame == frames.size()) {
                if(type.equals(AnimationType.LOOP)) {
                    currentFrame = 0;
                } else {
                    finished = true;
                    return;
                }
            }
            frames.get(currentFrame).apply(entity.getState());
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    public String getNext() {
        return next;
    }

    public AnimationType getType() {
        return type;
    }

    public static Animation loadAnimation(JsonObject data, Material itemMaterial, BakedAnimationEntity entity) {
        Map<String, Integer> states = new HashMap<>();
        AnimationType type;
        try {
            type = AnimationType.valueOf(data.get("loop").getAsString().toUpperCase());
        }catch (IllegalArgumentException ex) {
            type = AnimationType.ONCE;
        }
        int interval = (data.get("interval") == null? 1 : data.get("interval").getAsInt());
        String next = (data.has("next")?data.get("next").getAsString():null);
        Animation animation = new Animation(entity, type, next, interval);
        JsonArray frameData = data.get("frames").getAsJsonArray();
        for(int i = 0; i< frameData.size(); i++) {
            animation.addFrame(Frame.loadFrame(entity,animation,frameData.get(i).getAsJsonObject(),itemMaterial));
        }
        return animation;
    }
}
