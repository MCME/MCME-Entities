package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalVirtualEntityIsClose;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GoalEntityTargetDefend extends GoalEntityTarget {

    McmeEntity protege;

    public GoalEntityTargetDefend(GoalType type, VirtualEntity entity, Pathfinder pathfinder, McmeEntity target) {
        super(type, entity, pathfinder, target);
        this.protege = target;
    }

    @Override
    public void doTick() {
        super.doTick();
        if(target == protege && isCloseToTarget(GoalDistance.CAUTION)) {
//Logger.getGlobal().info("delete path as entity is close.");
            EntitiesPlugin.getEntityServer().handleEvent(new GoalVirtualEntityIsClose(getEntity(),this));
            setIsMoving(false);//deletePath();
            movementSpeed = MovementSpeed.STAND;
            setRotation(getEntity().getLocation().clone().setDirection(getTarget().getLocation().toVector()
                    .subtract(getEntity().getLocation().toVector())).getYaw());
        } else if(target != protege && isCloseToTarget(GoalDistance.ATTACK)) {
            EntitiesPlugin.getEntityServer().handleEvent(new GoalVirtualEntityIsClose(getEntity(),this));
            if(getEntity().getAttackCoolDown()==0) {
                getEntity().attack(target);
            }
        } else {
            setIsMoving(true);
            movementSpeed = MovementSpeed.WALK;
        }
        if(protege.isDead()) {
            setFinished();
        }
    }

    @Override
    public void update() {
        int range = 20;
        Set<McmeEntity> attackerSet = EntitiesPlugin.getEntityServer().getEntitiesAt(protege.getLocation(),range,range,range).stream()
                .filter(entity -> entity.getGoal() instanceof  GoalEntityTargetAttack && ((GoalEntityTargetAttack) entity.getGoal()).target == protege
                               || (entity.getGoal() instanceof GoalEntityTargetDefend && ((GoalEntityTargetDefend) entity.getGoal()).target == protege
                                   && ((GoalEntityTargetDefend) entity.getGoal()).target != ((GoalEntityTargetDefend) entity.getGoal()).protege))
                .collect(Collectors.toSet());
        attackerSet.addAll(getEntity().getAttackers());
        if(attackerSet.isEmpty()) {
            setTarget(protege);
        } else {
            List<McmeEntity> attackers = new ArrayList<>(attackerSet);
            McmeEntity closest = attackers.get(0);
            double minDist = getEntity().getLocation().distanceSquared(closest.getLocation());
            for(int i = 1; i < attackers.size(); i++) {
                double dist = getEntity().getLocation().distanceSquared(attackers.get(i).getLocation());
                if(dist < minDist) {
                    minDist = dist;
                    closest = attackers.get(i);
                }
            }
            if(minDist<range*range) {
                setTarget(closest);
            } else {
                setTarget(protege);
            }
        }
        //if(!((target == protege && isCloseToTarget(GoalDistance.CAUTION))
        //     || target != protege && isCloseToTarget(GoalDistance.ATTACK))) {
        super.update();
        //}
    }

}
