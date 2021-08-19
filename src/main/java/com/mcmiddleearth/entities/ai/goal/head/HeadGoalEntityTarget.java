package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.goal.GoalEntityTarget;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

import java.util.logging.Logger;

public class HeadGoalEntityTarget extends HeadGoal {

    private GoalEntityTarget goal;

    public HeadGoalEntityTarget(GoalEntityTarget goal) {
        this.goal = goal;
    }

    public HeadGoalEntityTarget(GoalEntityTarget goal, int duration) {
        this(goal);
        setDuration(duration);
    }

    public void setGoal(GoalEntityTarget goal) {
        this.goal = goal;
    }

    @Override
    public void doTick() {
        McmeEntity target = goal.getTarget();
        VirtualEntity entity = goal.getEntity();
        if(target!=null && !(target instanceof Placeholder)) {
            Location targetDir = entity.getLocation().clone()
                    .setDirection(target.getLocation().toVector()
                            .subtract(entity.getLocation().toVector()));
            yaw = targetDir.getYaw();
            pitch = targetDir.getPitch();
//Logger.getGlobal().info("Yaw: "+yaw +" pitch: "+pitch);
        }
    }

    @Override
    public boolean provideGoalAndEntity(Goal goal, McmeEntity entity) {
        if(goal instanceof GoalEntityTarget) {
            this.goal = (GoalEntityTarget) goal;
            return true;
        }
        return false;
    }
}
