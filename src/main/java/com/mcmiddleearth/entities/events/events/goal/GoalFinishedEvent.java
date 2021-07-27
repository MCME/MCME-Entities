package com.mcmiddleearth.entities.events.events.goal;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;

public class GoalFinishedEvent extends GoalEvent {

    public GoalFinishedEvent(McmeEntity entity, Goal currentGoal) {
        super(entity, currentGoal);
    }

}