package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

import java.util.Set;

public class GoalEntityTargetAttackClose extends GoalEntityTargetAttack {

    public GoalEntityTargetAttackClose(VirtualEntity entity, VirtualEntityGoalFactory factory, Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
        selectTarget();
    }

    @Override
    public void update() {
        selectTarget();
        super.update();
    }

    private void selectTarget() {
        if(getTarget() == null || isFinished() || !isCloseToTarget(GoalDistance.CAUTION)) {
            Set<McmeEntity> enemies = getEntity().getEnemies();
            if(!enemies.isEmpty()) {
                setTarget(enemies.stream().min((one, two) -> Double.compare(one.getLocation().distanceSquared(getEntity().getLocation()),
                        two.getLocation().distanceSquared(getEntity().getLocation()))).orElse(null));
                unsetFinished();
            }
        }
    }


}
