package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

import java.util.logging.Logger;

public class HeadGoalMimic extends HeadGoal {

    private McmeEntity mimic;
    private final VirtualEntity entity;

    public HeadGoalMimic(VirtualEntity entity, McmeEntity mimic, int duration) {
        this.entity = entity;
        this.mimic = mimic;
        setDuration(duration);
    }

    @Override
    public void doTick() {
        if (mimic != null) {
//Logger.getGlobal().info("Yaw: "+);
            yaw = mimic.getHeadYaw();
            pitch = mimic.getHeadPitch();
        }
    }

    @Override
    public boolean provideGoalAndEntity(Goal goal, McmeEntity entity) {
        if(entity != null) {
            this.mimic = entity;
            return true;
        }
        return false;
    }

}
