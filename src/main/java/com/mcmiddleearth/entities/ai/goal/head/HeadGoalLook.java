package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class HeadGoalLook extends HeadGoal {

    private final Location target;

    private final VirtualEntity entity;

    public HeadGoalLook(Location target, VirtualEntity entity) {
        this.target = target;
        this.entity = entity;
    }

    public HeadGoalLook(Location target, VirtualEntity entity, int duration) {
        this(target, entity);
        setDuration(duration);
    }

    @Override
    public void doTick() {
        Location targetDir = entity.getLocation().clone()
                .setDirection(target.toVector()
                        .subtract(entity.getLocation().toVector()));
        yaw = targetDir.getYaw();
        pitch = targetDir.getPitch();
    }

    public Location getTarget() {
        return target;
    }


}
