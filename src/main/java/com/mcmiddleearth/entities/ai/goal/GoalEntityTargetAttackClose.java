package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public class GoalEntityTargetAttackClose extends GoalEntityTargetAttack {

    public GoalEntityTargetAttackClose(GoalType type, VirtualEntity entity, Pathfinder pathfinder) {
        super(type, entity, pathfinder, null);
    }


}
