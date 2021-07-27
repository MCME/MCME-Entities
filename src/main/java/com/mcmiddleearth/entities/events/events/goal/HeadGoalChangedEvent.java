package com.mcmiddleearth.entities.events.events.goal;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.goal.head.HeadGoal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.Cancelable;

public class HeadGoalChangedEvent extends GoalVirtualEntityEvent{

    private HeadGoal nextHeadGoal;

    public HeadGoalChangedEvent(VirtualEntity entity, Goal goal, HeadGoal nextHeadGoal) {
        super(entity, goal);
        this.nextHeadGoal = nextHeadGoal;
    }

    public HeadGoal getNextHeadGoal() {
        return nextHeadGoal;
    }

    public void setNextHeadGoal(HeadGoal nextHeadGoal) {
        this.nextHeadGoal = nextHeadGoal;
    }

}
