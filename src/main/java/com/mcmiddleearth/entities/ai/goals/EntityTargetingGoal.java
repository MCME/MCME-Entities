package com.mcmiddleearth.entities.ai.goals;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Location;

public abstract class EntityTargetingGoal extends PathGoal {

    McmeEntity target;

    private float headYaw, headPitch;

    public EntityTargetingGoal(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder);
        this.target = target;
    }

    @Override
    public void doTick() {
        super.doTick();
        Location targetDir = getEntity().getLocation().clone()
                .setDirection(target.getLocation().toVector()
                        .subtract(getEntity().getLocation().toVector()));
        headYaw = targetDir.getYaw();
        headPitch = targetDir.getPitch();
    }

    @Override
    public void update() {
        setPathTarget(getTarget().getLocation().toVector());
        super.update();
    }

    @Override
    public boolean hasHeadRotation() {
        return true;
    }

    @Override
    public float getHeadYaw() {
        return headYaw;
    }

    @Override
    public float getHeadPitch() {
        return headPitch;
    }

    public McmeEntity getTarget() {
        return target;
    }

    public boolean isCloseToTarget(int distanceSquared) {
        return getEntity().getLocation().toVector().distanceSquared(getTarget().getLocation().toVector()) < distanceSquared;
    }
}
