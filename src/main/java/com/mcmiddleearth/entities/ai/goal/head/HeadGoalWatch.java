package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class HeadGoalWatch extends HeadGoal {

    private final McmeEntity target;

    private final VirtualEntity entity;

    public HeadGoalWatch(McmeEntity target, VirtualEntity entity) {
        this.target = target;
        this.entity = entity;
    }

    public HeadGoalWatch(McmeEntity target, VirtualEntity entity, int duration) {
        this(target, entity);
        setDuration(duration);
    }

    @Override
    public void doTick() {
        Location targetDir = entity.getLocation().clone()
                .setDirection(target.getLocation().toVector()
                        .subtract(entity.getLocation().toVector()));
        yaw = targetDir.getYaw();
        pitch = targetDir.getPitch();
    }

    public McmeEntity getTarget() {
        return target;
    }
}
