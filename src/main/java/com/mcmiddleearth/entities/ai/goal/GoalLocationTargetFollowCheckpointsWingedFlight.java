package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class GoalLocationTargetFollowCheckpointsWingedFlight extends GoalLocationTargetFollowCheckpoints {

    public GoalLocationTargetFollowCheckpointsWingedFlight(GoalType type, VirtualEntity entity, Pathfinder pathfinder,
                                                           Location[] checkpoints, boolean loop) {
        super(type, entity, pathfinder, checkpoints, loop);
    }

    @Override
    public Vector getDirection() {
        if((getEntity() instanceof WingedFlightEntity)
                && (getEntity().getMovementType().equals(MovementType.FLYING)|| getEntity().getMovementType().equals(MovementType.GLIDING))) {
            WingedFlightEntity winged = (WingedFlightEntity) getEntity();
            Location loc = winged.getLocation().clone();
            loc.setPitch(winged.getCurrentPitch());
            loc.setYaw(winged.getCurrentYaw());
            return loc.getDirection();
            /*double sinY = Math.sin(winged.getCurrentYaw());
            double cosY = Math.cos(winged.getCurrentYaw());
            double sinP = Math.sin(winged.getCurrentPitch());
            double cosP = Math.cos(winged.getCurrentPitch());
            return new Vector(cosY * sinP, sinY * sinP, cosP);*/
        } else {
            return super.getDirection();
        }
    }

    @Override
    public boolean isCloseToTarget(double distanceSquared) {
        double distance = getEntity().getLocation().toVector().distanceSquared(getTarget().toVector());
//Logger.getGlobal().info("Distance: "+distance);
        return distance < distanceSquared*400;
    }
}
