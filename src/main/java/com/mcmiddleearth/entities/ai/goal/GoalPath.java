package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.movement.RayTracer;
import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.ai.pathfinding.Pathfinder;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public abstract class GoalPath extends GoalVirtualEntity {

    private Path path;

    private Vector waypoint;

    private final Pathfinder pathfinder;

    private boolean hasRotation;
    private float rotation;

    protected final int isCloseDistanceSquared = 1;

    public GoalPath(GoalType type, VirtualEntity entity, Pathfinder pathfinder) {
        super(type, entity);
        this.pathfinder = pathfinder;
    }

    @Override
    public void update() {
        super.update();
//Logger.getGlobal().info("find path from: "+getEntity().getLocation().toVector().getBlockX()+" "
//        +getEntity().getLocation().toVector().getBlockY()+" "
//        +getEntity().getLocation().toVector().getBlockZ());
//Logger.getGlobal().info("find path to target: "+pathfinder.getTarget());
        findPath(getEntity().getLocation().toVector());
//Logger.getGlobal().info("Found path: "+path+" complete: "+path.isComplete());
        updateWaypoint();
//Logger.getGlobal().info("location: "+getEntity().getLocation().getX()+" "+getEntity().getLocation().getZ()+ " waypoint "+waypoint+" rotation: "+rotation);
    }

    @Override
    public void doTick() {
        super.doTick();
        hasRotation = false;
        if(waypoint != null && getEntity().getLocation().toVector().distanceSquared(waypoint) < isCloseDistanceSquared) {
            path.setStart(waypoint);
            updateWaypoint();
        }
    }

    public void deletePath() {
        path = null;
        waypoint = null;
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
        if(path == null || waypoint == null) {
            return null;
        } else {
            return waypoint.clone().subtract(getEntity().getLocation().toVector());
        }
    }

    @Override
    public boolean hasRotation() {
        return hasRotation;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
//Logger.getGlobal().info("*                           rotation: "+rotation);
        this.rotation = rotation;
        hasRotation = true;
    }

    public void updateWaypoint() {
//Logger.getGlobal().info("update Waypoint: start "+path+" "+path.getEnd());
        if(path!=null && path.getEnd()!=null) {
            int index = path.length()-1;
            while(index >= 0 && !isDirectWayClear(path.get(index))) {
//Logger.getGlobal().info("update Waypoint: "+index);
                index --;
            }
            if(index>=0) {
                waypoint = path.get(index).clone();
            }
//Logger.getGlobal().info("Waypoint: "+waypoint.getX()+" "+waypoint.getZ());
            if(waypoint != null) {
                setRotation(getEntity().getLocation().clone()
                        .setDirection(waypoint.clone().subtract(getEntity().getLocation().toVector())).getYaw());
            }
        }
    }

    private boolean isDirectWayClear(Vector target) {
        Vector targetDirection = target.clone().subtract(getEntity().getLocation().toVector());
//Logger.getGlobal().info("entity: "+getEntity().getLocation());
//Logger.getGlobal().info("target: "+target);
//Logger.getGlobal().info("Trace Vector: "+targetDirection);
        RayTracer<Double> tracer = new RayTracer<>(getEntity().getLocation().toVector(),targetDirection,
                (x,y,z) -> EntitiesPlugin.getEntityServer().getBlockProvider(getEntity().getLocation().getWorld().getUID())
                                       .blockTopY(x,y,z,getEntity().getJumpHeight()+1));
        BoundingBox boundingBox = getEntity().getBoundingBox().getBoundingBox();
//Logger.getGlobal().info("BB: min: "+boundingBox.getMin()+" max: "+boundingBox.getMax());
        int jumpHeight = getEntity().getJumpHeight();
        tracer.addRay(new Vector(boundingBox.getMinX(),boundingBox.getMinY(),boundingBox.getMinZ()));
        tracer.addRay(new Vector(boundingBox.getMinX(),boundingBox.getMinY(),boundingBox.getMaxZ()));
        tracer.addRay(new Vector(boundingBox.getMaxX(),boundingBox.getMinY(),boundingBox.getMinZ()));
        tracer.addRay(new Vector(boundingBox.getMaxX(),boundingBox.getMinY(),boundingBox.getMaxZ()));
        tracer.addRay(new Vector(boundingBox.getMinX(),boundingBox.getMaxY(),boundingBox.getMinZ()));
        tracer.addRay(new Vector(boundingBox.getMinX(),boundingBox.getMaxY(),boundingBox.getMaxZ()));
        tracer.addRay(new Vector(boundingBox.getMaxX(),boundingBox.getMaxY(),boundingBox.getMinZ()));
        tracer.addRay(new Vector(boundingBox.getMaxX(),boundingBox.getMaxY(),boundingBox.getMaxZ()));
        //tracer.trace();
        tracer.initTrace();
//Logger.getGlobal().info("Tracer: first "+tracer.first()+" last "+ tracer.last() + " stepX: "+tracer.stepX()+" stepZ: "+tracer.stepZ());
        int i = tracer.first();
        do {
//Logger.getGlobal().info("trace step");
            tracer.traceStep();
            RayTracer<Double>.RayTraceResultColumn current = tracer.current();//get(i);
            RayTracer<Double>.RayTraceResultColumn next = tracer.next();//get(i+1);
//Logger.getGlobal().info("Current: x: "+current.getBlockX()+" first: "+current.first()+" last: "+ current.last());
            int j = current.first();
            do {
/*if(current.hasNext(j)) {
    Logger.getGlobal().info("compare: z: " + j+":  "+ current.get(j) + " - " + current.getNext(j));
}
if(next != null && next.has(j)) {
    Logger.getGlobal().info("                              next: "+next.get(j)+" - "+current.get(j));
}*/
                if((current.hasNext(j) && current.getNext(j)-current.get(j)>jumpHeight)
                    || (next!=null && (next.has(j) && next.get(j)-current.get(j) > jumpHeight))) {
                    return false;
                }
                if(j != current.last()) {
                    j += tracer.stepZ();
                } else {
                    break;
                }
            } while(true);
            if(i != tracer.last()) {
                i += tracer.stepX();
            } else {
                break;
            }
        } while(true);
        return true;
    }

    public Path getPath() {
        return path;
    }

    public Vector getWaypoint() {
        return waypoint;
    }
}
