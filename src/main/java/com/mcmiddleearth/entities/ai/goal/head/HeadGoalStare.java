package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.GoalEntityTarget;

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
}
