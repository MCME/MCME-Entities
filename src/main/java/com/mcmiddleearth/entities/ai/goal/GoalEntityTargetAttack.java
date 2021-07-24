package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.AnimationType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public class GoalEntityTargetAttack extends GoalEntityTargetFollow {

    public GoalEntityTargetAttack(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder, target);
    }

    @Override
    public void doTick() {
        super.doTick();
        if(isCloseToTarget(isCloseDistanceSquared)) {
            if(getEntity().getAttackCoolDown()==0) {
                getEntity().attack(target);
            }
        }
    }

}
