package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class GoalPath extends GoalVirtualEntity {

    private Path path;

    private Vector waypoint;

    private final Pathfinder pathfinder;

    //private boolean hasRotation;

    private boolean isMoving = true;

    public GoalPath(VirtualEntity entity, VirtualEntityGoalFactory goalFactory, Pathfinder pathfinder) {
        super(entity, goalFactory);
        this.pathfinder = pathfinder;
    }

    @Override
    public void update() {
        super.update();
//Logger.getGlobal().info("find path from: "+getEntity().getLocation().toVector().getBlockX()+" "
//        +getEntity().getLocation().toVector().getBlockY()+" "
//        +getEntity().getLocation().toVector().getBlockZ());
//Logger.getGlobal().info("find path to target: "+pathfinder.getTarget());
        if(pathfinder.getTarget()!=null) {
            findPath(getEntity().getLocation().toVector());
            //Logger.getGlobal().info("Found path: \n"+path+" complete: "+path.isComplete());
            updateWaypoint();
        }
//Logger.getGlobal().info("location: "+getEntity().getLocation().getX()+" "+getEntity().getLocation().getZ()+ " waypoint "+waypoint+" rotation: "+rotation);
    }

    @Override
    public void doTick() {
        super.doTick();
        //hasRotation = false;
        if(waypoint != null && isMoving){//getEntity().getLocation().toVector().distanceSquared(waypoint) < isCloseDistanceSquared) {
            path.setStart(waypoint);
            updateWaypoint();
        }
    }

    public void deletePath() {
        path = null;
        waypoint = null;
    }

    public void setIsMoving(boolean move) {
        this.isMoving = move;
        /*if (move && waypoint != null) {
//Logger.getGlobal().info("Walk!");
            movementSpeed = MovementSpeed.WALK;
        } else {
            movementSpeed = MovementSpeed.STAND;
        }*/
        //return getEntity().getLocation().toVector().distanceSquared(waypoint) < isCloseDistanceSquared;
    }

    @Override
    public MovementSpeed getMovementSpeed() {
        if (isMoving && waypoint != null) {
//Logger.getGlobal().info("Walk!");
            return movementSpeed;
        } else {
            return MovementSpeed.STAND;
        }
    }

    public void setPathTarget(Vector target) {
        pathfinder.setTarget(target);
    }
    //public Pathfinder getPathfinder() {
    //    return pathfinder;
    //}

    public void findPath(Vector start) {
        path = pathfinder.getPath(start);
    }

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
            if (path == null || waypoint == null || !isMoving) {
                return null;
            } else {
                return waypoint.clone().subtract(getEntity().getLocation().toVector());
            }
        }
    }

    /*@Override
    public boolean hasRotation() {
        return hasRotation;
    }

    public void setRotation(float rotation) {
//Logger.getGlobal().info("*                           rotation: "+rotation);
        this.rotation = rotation;
        hasRotation = true;
    }*/

    public void updateWaypoint() {
//Logger.getGlobal().info("update Waypoint: start "+path+" "+path.getEnd());
        if(path!=null) {
            /*if(path.getTarget()!=null && isDirectWayClear(path.getTarget())) {
                if(getEntity().getLocation().toVector().distanceSquared(path.getTarget()) < GoalDistance.POINT
                    || getEntity().getLocation().toVector().distanceSquared(path.getEnd())) {
                    waypoint = null;
Logger.getGlobal().info("Path target reached!");
                } else {
                    waypoint = path.getTarget().clone();
                }
            } else*/
            if(path.getEnd()!=null) {
                if(getEntity().getLocation().toVector().distanceSquared(path.getEnd()) < GoalDistance.POINT) {
                    waypoint = null;
                } else {
                    int index = path.length() - 1;
                    while (index >= 0 && !pathfinder.isDirectWayClear(path.get(index))) {
                        //Logger.getGlobal().info("update Waypoint: "+index);
                        index--;
                    }
                    if (index >= 0) {
                        waypoint = path.get(index).clone();
                    }
                }
            } else {
                waypoint = null;
            }
//Logger.getGlobal().info("Waypoint: "+waypoint.getX()+" "+waypoint.getZ());
            if(waypoint != null && isMoving) {
                Location orientation = getEntity().getLocation().clone()
                        .setDirection(waypoint.clone().subtract(getEntity().getLocation().toVector()));
                setYaw(orientation.getYaw());
                setPitch(orientation.getPitch());
            }
        }
    }

    public Path getPath() {
        return path;
    }

    public Vector getWaypoint() {
        return waypoint;
    }

}
