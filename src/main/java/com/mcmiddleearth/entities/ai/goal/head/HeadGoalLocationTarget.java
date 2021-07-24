package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.GoalEntityTarget;
import com.mcmiddleearth.entities.ai.goal.GoalLocationTarget;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class HeadGoalLocationTarget extends HeadGoal {

    private final GoalLocationTarget goal;

    public HeadGoalLocationTarget(GoalLocationTarget goal) {
        this.goal = goal;
    }

    public HeadGoalLocationTarget(GoalLocationTarget goal, int duration) {
        this(goal);
        setDuration(duration);
    }

    @Override
    public void doTick() {
        Location target = goal.getTarget();
        VirtualEntity entity = goal.getEntity();
        if(target !=null) {
            Location targetDir = entity.getLocation().clone()
                    .setDirection(target.toVector()
                            .subtract(entity.getLocation().toVector()));
            yaw = targetDir.getYaw();
            pitch = targetDir.getPitch();
        }
    }
}
