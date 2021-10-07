package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public class HeadGoalMimic extends HeadGoal {

    private McmeEntity mimic;
    private VirtualEntity entity;

    public HeadGoalMimic(VirtualEntity entity, McmeEntity mimic) {
        this.entity = entity;
        this.mimic = mimic;
    }

    @Override
    public void doTick() {
        if (mimic != null) {
            entity.setHeadRotation(mimic.getHeadYaw(), mimic.getHeadPitch());
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
