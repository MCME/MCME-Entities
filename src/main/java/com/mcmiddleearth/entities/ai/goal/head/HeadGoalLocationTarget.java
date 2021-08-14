package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.goal.GoalLocationTarget;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class HeadGoalLocationTarget extends HeadGoal {

    private GoalLocationTarget goal;

    public HeadGoalLocationTarget(GoalLocationTarget goal) {
        this.goal = goal;
    }

    public HeadGoalLocationTarget(GoalLocationTarget goal, int duration) {
        this(goal);
        setDuration(duration);
    }

    public void setGoal(GoalLocationTarget goal) {
        this.goal = goal;
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

    @Override
    public boolean provideGoalAndEntity(Goal goal, McmeEntity entity) {
        if(goal instanceof GoalLocationTarget) {
            this.goal = (GoalLocationTarget) goal;
            return true;
        }
        return false;
    }

}
