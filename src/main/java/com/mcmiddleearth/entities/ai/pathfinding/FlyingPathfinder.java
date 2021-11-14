package com.mcmiddleearth.entities.ai.pathfinding;

import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import org.bukkit.util.Vector;

public class FlyingPathfinder implements Pathfinder {

    private Vector target;

    private final WingedFlightEntity entity;

    boolean homing = false;

    public FlyingPathfinder(WingedFlightEntity entity) {
        this.entity = entity;
    }

    @Override
    public Path getPath(Vector start) {
        Path result = new Path(target);
        result.addPoint(start);

        double rotation = entity.getMaxRotationStepFlight();
        double speed = entity.getVelocity().length();
        double minRadius = speed / (rotation*Math.PI/180);
//Logger.getGlobal().info("Rotation: "+rotation+" speed: "+speed);
        double requiredRadius = calculateRequiredRadius();
        if(homing && Math.abs(requiredRadius)<minRadius*1.4) {
            homing = false;
        } else if(!homing && Math.abs(requiredRadius)>minRadius*2) {
            homing = true;
        }
        if(homing) {
//Logger.getGlobal().info("Radius: "+requiredRadius+" "+minRadius+" -----Target: "+target);
            result.addPoint(target);
        } else {
            Vector point = start.clone().add(new Vector(entity.getVelocity().getZ(),0,entity.getVelocity().getX())
                    .normalize().multiply(Math.signum(requiredRadius)*minRadius));
            point.setY(target.getY());
            result.addPoint(point);
//Logger.getGlobal().info("Radius: "+requiredRadius+" "+minRadius+" -----Point: "+point);
        }
        return result;
    }

    @Override
    public void setTarget(Vector target) {
        this.target = target;
    }

    @Override
    public Vector getTarget() {
        return target;
    }

    @Override
    public boolean isDirectWayClear(Vector target) {
        return true;
    }

    private double calculateRequiredRadius() {
        Vector v = entity.getVelocity().normalize();
        Vector a = entity.getLocation().toVector();
        Vector b = target;
        return ((a.getX()-b.getX())*(a.getX()-b.getX())+(a.getZ()-b.getZ())*(a.getZ()-b.getZ()))
             / (2 * (v.getX()*(a.getZ()-b.getZ())-v.getZ()*(a.getX()-b.getX())));
    }
}
