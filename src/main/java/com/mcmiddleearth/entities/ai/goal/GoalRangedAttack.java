package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.entity.EntityType;

public class GoalRangedAttack extends GoalWatchEntity {

    private final McmeEntityType projectileType = new McmeEntityType(EntityType.ARROW);

    public GoalRangedAttack(VirtualEntity entity, VirtualEntityGoalFactory factory) {
        super(entity, factory);
    }

    @Override
    public void doTick() {
        super.doTick();
        if(!targetIncomplete) {
            if (!isFinished()) {
                getEntity().shoot(target, projectileType);
            }
            if (target.isDead()) {
                setFinished();
            }
        }
    }
}
