package com.mcmiddleearth.entities.ai.movement;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.GoalJockey;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Projectile;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.provider.BlockProvider;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;

public class MovementEngine {

    private final Vector gravity = new Vector(0,-0.2,0); //theoretically -0.5

    private final VirtualEntity entity;

    private final BlockProvider blockProvider;

    private double fallStart = 0;

    private static final Vector zero = new Vector(0,0,0);

    public MovementEngine(VirtualEntity entity) {
        this.entity = entity;
        this.blockProvider = EntitiesPlugin.getEntityServer().getBlockProvider(entity.getLocation().getWorld().getUID());
    }

    public void calculateMovement(Vector direction) {
//Logger.getGlobal().info("direction: "+direction);
        /*if ((direction == null || direction.equals(new Vector(0,0,0))) && !entity.getMovementType().equals(MovementType.FALLING)) {
            entity.setVelocity(new Vector(0, 0, 0));
            return;
        }*/
        if(direction == null) direction = zero.clone();
        if(entity.isDead()) {
            switch (entity.getMovementType()) {
                case FLYING:
                case GLIDING:
                    entity.setMovementType(MovementType.FALL);
                    break;
                case UPRIGHT:
                case SNEAKING:
                    entity.setVelocity(new Vector(0, 0, 0));
                    return;
            }
        }
        switch(entity.getMovementType()) {
            case FLYING:
//Logger.getGlobal().info("location: "+ entity.getLocation());
//Logger.getGlobal().info("speed: "+ entity.getFlyingSpeed());
                Vector velocity = zero.clone();
                if(!direction.equals(zero)) {
                    velocity = direction.normalize().multiply(entity.getFlyingSpeed());
                }
//Logger.getGlobal().info("velocity: "+ velocity);
                //TODO better pathfinding for flying entities
                /*if(cannotMove(velocity)) {
                    velocity = new Vector(0,0,0);
//Logger.getGlobal().info("cant move");
                }*/
                //end TODO
//Logger.getGlobal().info("speed: "+getFlyingSpeed()+" velocity: "+velocity);
                entity.setVelocity(velocity);
                break;
            case FALL:
//Logger.getGlobal().info("falling entity vel: "+entity.getVelocity());
                velocity = entity.getVelocity().clone().add(gravity);
//Logger.getGlobal().info("FALLING: "+velocity);
                if(cannotMove(velocity)) {
//Logger.getGlobal().info("cannot move 1: "+distanceToGround());
                    double groundDistance = distanceToGround();
                    if (groundDistance < -velocity.getY()) {
//Logger.getGlobal().info("WALK");
                        velocity.setY(-groundDistance);
                        double fallHeight = fallStart - (entity.getEntityBoundingBox().getMin().getY()-groundDistance);
//Logger.getGlobal().info("Fall Damage?: "+fallStart +" entity y: "+entity.getBoundingBox().getMin().getY()+" ground Dist: "+ groundDistance+" fallHeight: "+fallHeight+" "+entity.getFallDepth());
                        if(fallHeight>entity.getFallDepth()+0.5) {
                            entity.damage((int) (fallHeight - entity.getFallDepth()));
                        }
                        if(entity.isSneaking()) {
                            entity.setMovementType(MovementType.SNEAKING);
                        } else {
                            entity.setMovementType(MovementType.UPRIGHT);
                        }
                    } else {
//if (velocity.getY()<-10) {
//    Logger.getGlobal().info("Warning high fall velocity: "+velocity.getY());
//}
                    //if(cannotMove(velocity)) {
//Logger.getGlobal().info("horizontal null");
                        velocity.setX(0);
                        velocity.setZ(0);
                    /*} else {
                        entity.setMovementType(MovementType.WALKING);*/
                    }
                }
                entity.setVelocity(velocity);
//Logger.getGlobal().info("falling entity vel: "+entity.getVelocity());
                break;
            case UPRIGHT:
            case SNEAKING:
            default:
                velocity = zero.clone();
                if(!direction.equals(zero)) {
                    velocity = direction.normalize().multiply(entity.getGenericSpeed());
                }
//Logger.getGlobal().info("vector: "+velocity.getX()+" "+velocity.getY()+" "+velocity.getZ());
                velocity.setY(0);
                Vector collisionVelocity = handleCollisions(velocity.clone());
//Logger.getGlobal().info("colision vec: "+collisionVelocity.getX()+" "+collisionVelocity.getY()+" "+collisionVelocity.getZ());
                if(!cannotMove(collisionVelocity)) {
                    velocity = collisionVelocity;
                }
                if(cannotMove(velocity)) {
                    double jumpHeight = jumpHeight();
                    if(jumpHeight>0 && jumpHeight<= entity.getJumpHeight()+0.01) {
                        entity.setMovementType(MovementType.FALL);
                        velocity.setY(Math.sqrt(-2 * jumpHeight * gravity.getY()));
//if (!Double.isFinite(velocity.getY()) || velocity.getY()>10 || jumpHeight>1.2) {
//    Logger.getGlobal().info("Warning! Wrong velocity: "+velocity.getY()+" jump: "+jumpHeight+" maxtJump: "+entity.getJumpHeight()+" gravity: "+gravity.getY());
//}
//Logger.getGlobal().info("entity vel: "+entity.getVelocity());
                    } else {
                        velocity = new Vector(0,0,0);
                    }
                } else if(distanceToGround()>0.01) {
                    if(!cannotMove(velocity.clone().add(gravity))) {
                        entity.setMovementType(MovementType.FALL);
                    }
                }
                entity.setVelocity(velocity);
                break;
        }
    }

    public static boolean checkFinite(Vector vector) {
        return Double.isFinite(vector.getX()) && Double.isFinite(vector.getY()) && Double.isFinite(vector.getZ());
    }

    public boolean cannotMove(Vector velocity) {
        BoundingBox entityBB = entity.getEntityBoundingBox().getBoundingBox().clone();
//Logger.getGlobal().info("bb x1 "+entityBB.getMinX()+ " x2 "+entityBB.getMaxX()+" vec x: "+velocity.getX());
//Logger.getGlobal().info("velo: "+velocity);
        entityBB.shift(velocity);
        for (int i = getBlock(entityBB.getMinX()); i <= getBlock(entityBB.getMaxX()); i++) {
            for (int j = getBlock(entityBB.getMinY()); j <= getBlock(entityBB.getMaxY()); j++) {
                for (int k = getBlock(entityBB.getMinZ()); k <= getBlock(entityBB.getMaxZ()); k++) {
                    if (!blockProvider.isPassable(i, j, k)
                            && entityBB.overlaps(blockProvider.getBoundingBox(i, j, k))) {
//Logger.getGlobal().info("bb min "+entityBB.getMin()+ " max "+entityBB.getMax());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Vector handleCollisions(Vector velocity) {
        if(entity.getEntityBoundingBox().isZero()) return velocity;
        BoundingBox entityBB = entity.getEntityBoundingBox().getBoundingBox().clone();
        Collection<McmeEntity> closeEntities = EntitiesPlugin.getEntityServer()
                                        .getEntitiesAt(entity.getLocation(), (int) (entityBB.getWidthX()*2+1),
                                                                             (int)(entityBB.getHeight()*2+1),
                                                                             (int)(entityBB.getWidthZ()*2+1));
        for(McmeEntity search: closeEntities) {
/*if(search instanceof RealPlayer) {
    Logger.getGlobal().info("player: "+search.getBoundingBox().isZero());
    search.getBoundingBox().setLocation(search.getLocation());
    Logger.getGlobal().info("max: "+search.getBoundingBox().getMax());
    Logger.getGlobal().info("min: "+search.getBoundingBox().getMin());
    Logger.getGlobal().info("bb: "+search.getBoundingBox().getBoundingBox());
}
Logger.getGlobal().info("type: "+search.getClass().getSimpleName()+" "+search.getType().name());*/
            if(!((search.getGoal() instanceof GoalJockey) && ((GoalJockey)search.getGoal()).getSteed().equals(entity))
                    && !(search instanceof Projectile)
                    && search != entity && search.getEntityBoundingBox() != null && !search.getEntityBoundingBox().isZero()
                    && entityBB.overlaps(search.getEntityBoundingBox().getBoundingBox())) {
                double speed = velocity.length();
//Logger.getGlobal().info("colision: "+search);
                if(Double.isFinite(speed)) {
                    speed = Math.max(speed,0.01);
                    Vector distance = search.getLocation().toVector().subtract(entity.getLocation().toVector());
//Logger.getGlobal().info("distance: "+distance.getX()+" "+distance.getY()+" "+distance.getZ());
                    distance.setY(0);
                    if(distance.getX()==0 && distance.getZ()==0) {
                        distance = Vector.getRandom().setY(0);
                    }
                    //Vector oldDistance = distance.clone();
                    distance.normalize().multiply(speed);
                    velocity.subtract(distance).normalize().multiply(speed);
                }
            }
//Logger.getGlobal().info("next");
        }
        if(Double.isFinite(velocity.getX()) && Double.isFinite(velocity.getY()) && Double.isFinite(velocity.getZ())) {
            return velocity;
        } else {
            return new Vector(0,0,0);
        }
    }

    public double distanceToGround() {
        BoundingBox entityBB = entity.getEntityBoundingBox().getBoundingBox().clone();
        return distanceToGround(entityBB, (int)entity.getJumpHeight()+1);
    }

    private double distanceToGround(BoundingBox boundingBox, int range) {
        double distance = Double.MAX_VALUE;
        for (int i = getBlock(boundingBox.getMinX()); i <= getBlock(boundingBox.getMaxX()); i++) {
            for (int j = getBlock(boundingBox.getMinZ()); j <= getBlock(boundingBox.getMaxZ()); j++) {
                int y = getBlock(boundingBox.getMinY());
                double thisDistance = boundingBox.getMinY() - blockProvider.blockTopY(i,y,j, range);
                if(thisDistance < distance){
                    distance = thisDistance;
                }
            }
        }
        return distance;
    }

    private int getBlock(double cord) {
        return (int) Math.floor(cord);
    }


    public double jumpHeight() {
        BoundingBox entityBB = entity.getEntityBoundingBox().getBoundingBox().clone();
        entityBB.shift(new Vector(entity.getVelocity().getX(),0,entity.getVelocity().getZ()));
        return - distanceToGround(entityBB, (int)entity.getJumpHeight()+1);
    }

    public void setFallStart(double fallStart) {
        this.fallStart = fallStart;
    }
}
