package com.mcmiddleearth.entities.ai.movement;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.provider.BlockProvider;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.logging.Logger;

public class MovementEngine {

    private final Vector gravity = new Vector(0,-0.2,0); //theoretically -0.5

    private final VirtualEntity entity;

    private final BlockProvider blockProvider;

    private double fallStart = 0;

    public MovementEngine(VirtualEntity entity) {
        this.entity = entity;
        this.blockProvider = EntitiesPlugin.getEntityServer().getBlockProvider(entity.getLocation().getWorld().getUID());
    }

    public void calculateMovement(Vector direction) {
//Logger.getGlobal().info("direction: "+direction);
        if ((direction == null || direction.equals(new Vector(0,0,0))) && !entity.getMovementType().equals(MovementType.FALLING)) {
            entity.setVelocity(new Vector(0, 0, 0));
            return;
        }
        if(entity.isDead()) {
            switch (entity.getMovementType()) {
                case FLYING:
                case GLIDING:
                    entity.setMovementType(MovementType.FALLING);
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
//Logger.getGlobal().info("speed: "+ getFlyingSpeed());
                Vector velocity = direction.normalize().multiply(getFlyingSpeed());
//Logger.getGlobal().info("velocity: "+ velocity);
                if(cannotMove(velocity)) {
                    velocity = new Vector(0,0,0);
//Logger.getGlobal().info("cant move");
                }
//Logger.getGlobal().info("speed: "+getFlyingSpeed()+" velocity: "+velocity);
                entity.setVelocity(velocity);
                break;
            case FALLING:
//Logger.getGlobal().info("falling entity vel: "+entity.getVelocity());
                velocity = entity.getVelocity().clone().add(gravity);
//Logger.getGlobal().info("FALLING: "+velocity);
                if(cannotMove(velocity)) {
//Logger.getGlobal().info("cannot move 1: "+distanceToGround());
                    double groundDistance = distanceToGround();
                    if (groundDistance < -velocity.getY()) {
//Logger.getGlobal().info("WALK");
                        velocity.setY(-groundDistance);
                        double fallHeight = fallStart - (entity.getBoundingBox().getMin().getY()-groundDistance);
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
if (velocity.getY()<-10) {
    Logger.getGlobal().info("Warning high fall velocity: "+velocity.getY());
}
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
                velocity = direction.normalize().multiply(getGenericSpeed());
                velocity.setY(0);
                Vector collisionVelocity = handleCollisions(velocity.clone());
                if(!cannotMove(collisionVelocity)) {
                    velocity = collisionVelocity;
                }
                if(cannotMove(velocity)) {
                    double jumpHeight = jumpHeight();
                    if(jumpHeight>0 && jumpHeight<= getMaxJumpHeight()+0.01) {
                        entity.setMovementType(MovementType.FALLING);
                        velocity.setY(Math.sqrt(-2 * jumpHeight * gravity.getY()));
if (!Double.isFinite(velocity.getY()) || velocity.getY()>10 || jumpHeight>1.2) {
    Logger.getGlobal().info("Warning! Wrong velocity: "+velocity.getY()+" jump: "+jumpHeight+" maxtJump: "+getMaxJumpHeight()+" gravity: "+gravity.getY());
}
//Logger.getGlobal().info("entity vel: "+entity.getVelocity());
                    } else {
                        velocity = new Vector(0,0,0);
                    }
                } else if(distanceToGround()>0.01) {
                    if(!cannotMove(velocity.clone().add(gravity))) {
                        entity.setMovementType(MovementType.FALLING);
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
        BoundingBox entityBB = entity.getBoundingBox().getBoundingBox().clone();
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
        BoundingBox entityBB = entity.getBoundingBox().getBoundingBox().clone();
        Collection<McmeEntity> closeEntities = EntitiesPlugin.getEntityServer()
                                        .getEntitiesAt(entity.getLocation(), (int) (entityBB.getWidthX()*2+1),
                                                                             (int)(entityBB.getHeight()*2+1),
                                                                             (int)(entityBB.getWidthZ()*2+1));
        for(McmeEntity search: closeEntities) {
            if(search != entity
                    && entityBB.overlaps(search.getBoundingBox().getBoundingBox())) {
                double speed = velocity.length();
                if(Double.isFinite(speed)) {
                    speed = Math.max(speed,0.01);
                    Vector distance = search.getLocation().toVector().subtract(entity.getLocation().toVector());
                    distance.setY(0);
                    //Vector oldDistance = distance.clone();
                    distance.normalize().multiply(speed);
                    velocity.subtract(distance).normalize().multiply(speed);
                }
            }
        }
        return velocity;
    }

    public double distanceToGround() {
        BoundingBox entityBB = entity.getBoundingBox().getBoundingBox().clone();
        return distanceToGround(entityBB, entity.getJumpHeight()+1);
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
        BoundingBox entityBB = entity.getBoundingBox().getBoundingBox().clone();
        entityBB.shift(new Vector(entity.getVelocity().getX(),0,entity.getVelocity().getZ()));
        return - distanceToGround(entityBB, entity.getJumpHeight()+1);
    }

    private double getFlyingSpeed() {
        AttributeInstance instance = entity.getAttribute(Attribute.GENERIC_FLYING_SPEED);
        if(instance == null) {
            return getGenericSpeed();
        }
        return instance.getValue();
    }

    private double getGenericSpeed() {
        AttributeInstance instance = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        return (instance!=null?instance.getValue():0.1);
    }

    private double getMaxJumpHeight() {
        AttributeInstance instance = entity.getAttribute(Attribute.HORSE_JUMP_STRENGTH);
        return (instance!=null?instance.getValue():1);
    }

    public void setFallStart(double fallStart) {
        this.fallStart = fallStart;
    }
}
