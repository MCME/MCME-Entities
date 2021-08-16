package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class HeadGoalWatch extends HeadGoal {

    private final McmeEntity target;

    private McmeEntity entity;

    public HeadGoalWatch(McmeEntity target, McmeEntity entity) {
        this.target = target;
        this.entity = entity;
    }

    public HeadGoalWatch(McmeEntity target, McmeEntity entity, int duration) {
        this(target, entity);
        setDuration(duration);
    }

    public void setEntity(VirtualEntity entity) {
        this.entity = entity;
    }

    @Override
    public void doTick() {
        if(target != null && !(target instanceof Placeholder)) {
            Location targetDir = entity.getLocation().clone()
                    .setDirection(target.getLocation().toVector()
                            .subtract(entity.getLocation().toVector()));
            yaw = targetDir.getYaw();
            pitch = targetDir.getPitch();
        }
    }

    public McmeEntity getTarget() {
        return target;
    }

    @Override
    public boolean provideGoalAndEntity(Goal goal, McmeEntity entity) {
        if(entity != null) {
            this.entity = entity;
            return true;
        }
        return false;
    }


}
