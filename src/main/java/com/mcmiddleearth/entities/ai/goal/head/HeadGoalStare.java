package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public class HeadGoalStare extends HeadGoal {

    public HeadGoalStare(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public HeadGoalStare(float yaw, float pitch, int duration) {
        this(yaw, pitch);
        setDuration(duration);
    }

    @Override
    public void doTick() {}

    @Override
    public boolean provideGoalAndEntity(Goal goal, McmeEntity entity) {
        return true;
    }

    @Override
    public boolean hasHeadRotation() {return false;}
}
