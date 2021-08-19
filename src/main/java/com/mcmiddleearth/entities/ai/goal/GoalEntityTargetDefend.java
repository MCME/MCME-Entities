package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Placeholder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.goal.GoalVirtualEntityIsClose;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GoalEntityTargetDefend extends GoalEntityTarget {

    private McmeEntity protege;
    private boolean protegeIncomplete = false;

    public GoalEntityTargetDefend(VirtualEntity entity, VirtualEntityGoalFactory factory, Pathfinder pathfinder) {
        super(entity, factory, pathfinder);
        this.protege = factory.getTargetEntity();
        if(this.protege instanceof Placeholder) {
            protegeIncomplete = true;
        }
    }

    @Override
    public void doTick() {
        super.doTick();
        if(!targetIncomplete) {
            if (target == protege && isCloseToTarget(GoalDistance.CAUTION)) {
                //Logger.getGlobal().info("delete path as entity is close.");
                EntitiesPlugin.getEntityServer().handleEvent(new GoalVirtualEntityIsClose(getEntity(), this));
                setIsMoving(false);//deletePath();
                movementSpeed = MovementSpeed.STAND;
                Location orientation = getEntity().getLocation().clone().setDirection(getTarget().getLocation().toVector()
                        .subtract(getEntity().getLocation().toVector()));
                setYaw(orientation.getYaw());
                setPitch(orientation.getPitch());
            } else if (target != protege && isCloseToTarget(GoalDistance.ATTACK)) {
                EntitiesPlugin.getEntityServer().handleEvent(new GoalVirtualEntityIsClose(getEntity(), this));
                if (getEntity().getAttackCoolDown() == 0) {
                    getEntity().attack(target);
                }
            } else {
                setIsMoving(true);
                movementSpeed = MovementSpeed.WALK;
            }
            if (!protegeIncomplete && protege.isDead()) {
                setFinished();
            }
        }
    }

    @Override
    public void update() {
        if(protegeIncomplete) {
            McmeEntity search = EntitiesPlugin.getEntityServer().getEntity(protege.getUniqueId());
            if(search != null) {
                protege = search;
                protegeIncomplete = false;
            }
        }
        int range = 20;
        Set<McmeEntity> attackerSet = EntitiesPlugin.getEntityServer().getEntitiesAt(protege.getLocation(),range,range,range).stream()
                .filter(entity -> entity.getGoal() instanceof  GoalEntityTargetAttack && ((GoalEntityTargetAttack) entity.getGoal()).target == protege
                               || (entity.getGoal() instanceof GoalEntityTargetDefend && ((GoalEntityTargetDefend) entity.getGoal()).target == protege
                                   && ((GoalEntityTargetDefend) entity.getGoal()).target != ((GoalEntityTargetDefend) entity.getGoal()).protege))
                .collect(Collectors.toSet());
        attackerSet.addAll(getEntity().getEnemies());
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

    @Override
    public VirtualEntityGoalFactory getFactory() {
        return super.getFactory().withTargetEntity(protege);
    }
}
