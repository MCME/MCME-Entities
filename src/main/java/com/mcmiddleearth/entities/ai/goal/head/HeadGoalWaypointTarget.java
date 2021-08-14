package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.goal.GoalPath;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class HeadGoalWaypointTarget extends HeadGoal {

    private GoalPath goal;

    public HeadGoalWaypointTarget(GoalPath goal) {
        this.goal = goal;
    }

    public HeadGoalWaypointTarget(GoalPath goal, int duration) {
        this(goal);
        setDuration(duration);
    }

    public void setGoal(GoalPath goal) {
        this.goal = goal;
    }

    @Override
    public void doTick() {
        Vector target = goal.getWaypoint();
        VirtualEntity entity = goal.getEntity();
        if(target != null) {
            Location targetDir = entity.getLocation().clone()
                    .setDirection(target.clone()
                            .subtract(entity.getLocation().toVector()));
            yaw = targetDir.getYaw();
            pitch = targetDir.getPitch();
        }
    }

    @Override
    public boolean hasHeadRotation() {
//Logger.getGlobal().info("Has waypoint: "+goal.getWaypoint());
        return goal.getWaypoint() != null;
    }

    @Override
    public boolean provideGoalAndEntity(Goal goal, McmeEntity entity) {
        if(goal instanceof GoalPath) {
            this.goal = (GoalPath) goal;
            return true;
        }
        return false;
    }

}
