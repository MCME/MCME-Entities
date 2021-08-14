package com.mcmiddleearth.entities.ai.goal.head;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public class HeadGoalLook extends HeadGoal {

    private final Location target;

    private McmeEntity entity;

    public HeadGoalLook(Location target, McmeEntity entity) {
        this.target = target;
        this.entity = entity;
    }

    public HeadGoalLook(Location target, McmeEntity entity, int duration) {
        this(target, entity);
        setDuration(duration);
    }

    public void setEntity(VirtualEntity entity) {
        this.entity = entity;
    }

    @Override
    public void doTick() {
        if(entity instanceof Placeholder) {
            McmeEntity search = EntitiesPlugin.getEntityServer().getEntity(entity.getUniqueId());
            if(search != null) {
                entity = search;
            }
        }
        Location targetDir = entity.getLocation().clone()
                .setDirection(target.toVector()
                        .subtract(entity.getLocation().toVector()));
        yaw = targetDir.getYaw();
        pitch = targetDir.getPitch();
    }

    public Location getTarget() {
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
