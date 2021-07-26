package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.AnimationType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;

import java.util.logging.Logger;

public class GoalEntityTargetAttack extends GoalEntityTarget {

    public GoalEntityTargetAttack(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder, target);
    }

    @Override
    public void doTick() {
        super.doTick();
        if(isCloseToTarget(GoalDistance.ATTACK)) {
//Logger.getGlobal().info("delete path as entity is close.");
            setIsMoving(false);//deletePath();
            setRotation(getEntity().getLocation().clone().setDirection(getTarget().getLocation().toVector()
                    .subtract(getEntity().getLocation().toVector())).getYaw());
//Logger.getGlobal().info("Cooldown: "+getEntity().getAttackCoolDown());
            if(getEntity().getAttackCoolDown()==0) {
//Logger.getGlobal().info("ATTACK");
                getEntity().attack(target);
            }
        } else {
            setIsMoving(true);
        }
    }

    /*@Override
    public void update() {
        if(!(isCloseToTarget(GoalDistance.CAUTION))) {
            super.update();
        }
    }*/

    @Override
    public boolean isFinished() { return target.isDead();}


}
