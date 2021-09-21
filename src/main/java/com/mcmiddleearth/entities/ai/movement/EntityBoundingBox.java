package com.mcmiddleearth.entities.ai.movement;

import com.mcmiddleearth.entities.api.McmeEntityType;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Objects;

public class EntityBoundingBox {

    private Vector min, max;
    private final Vector dMin, dMax;

    private final boolean zeroSize;

    public EntityBoundingBox(double dx, double dz, double dyMin, double dyMax) {
        dMin = new Vector(-dx,-dyMin,-dz);
        dMax = new Vector(dx, dyMax,dz);
        zeroSize = dx == 0 && dz == 0 && dyMin == dyMax;
    }

    public EntityBoundingBox(EntityBoundingBox other) {
        dMin = other.dMin.clone();
        dMax = other.dMax.clone();
        min = (other.min!=null?other.min.clone():null);
        max = (other.max!=null?other.max.clone():null);
        zeroSize = other.zeroSize;
    }

    public void setLocation(Location location) {
        min = location.toVector().add(dMin);
        max = location.toVector().add(dMax);
    }

    public Vector getLocation() {
        if(min!=null) {
            return min.clone().subtract(dMin);
        } else {
            return null;
        }
    }

    public BoundingBox getBoundingBox() {
        return new BoundingBox(min.getX(),min.getY(),min.getZ(),max.getX(),max.getY(), max.getZ());
    }

    public boolean isZero() {
        return zeroSize;
    }
    public Vector getMin() {
        return min;
    }

    public Vector getMax() {
        return max;
    }

    public double getDx() {
        return dMax.getX();
    }

    public double getDz() {
        return dMax.getZ();
    }

    public double getYMin() {
        return -dMin.getY();
    }

    public double getYMax() {
        return dMax.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityBoundingBox that = (EntityBoundingBox) o;
        return Objects.equals(min, that.min) &&
                Objects.equals(max, that.max) &&
                dMin.equals(that.dMin) &&
                dMax.equals(that.dMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, dMin, dMax);
    }

    /*public boolean canMove(Vector velocity) {
        int minX = (int) (min.getX()+velocity.getX());
        int minY = (int) (min.getY()+velocity.getY());
        int minZ = (int) (min.getZ()+velocity.getZ());
        int maxX = (int) (max.getX()+velocity.getX());
        int maxY = (int) (max.getY()+velocity.getY());
        int maxZ = (int) (max.getZ()+velocity.getZ());
        return checkPassable(minX,minY, minZ)
                && checkPassable(minX,minY,maxZ)
                && checkPassable(minX,maxY,minZ)
                && checkPassable(minX,maxY,maxZ)
                && checkPassable(maxX,minY,minZ)
                && checkPassable(maxX,minY,maxZ)
                && checkPassable(maxX,maxY,minZ)
                && checkPassable(maxX,maxY,maxZ);
    }

    public double distanceToGround() {
        int minX = (int) min.getX();
        int maxX = (int) max.getX();
        int minZ = (int) min.getZ();
        int maxZ = (int) max.getZ();
        int minY = (int) min.getY();
        return min.getY() - minY
                + Math.min(distanceToGround(minX, minY, minZ), Math.min(distanceToGround(minX, minY, maxZ),
                  Math.min(distanceToGround(maxX, minY, minZ),distanceToGround(maxX, minY, maxZ))));
    }

    private int distanceToGround(int x, int y, int z) {
        int distance = 0;
        if(!checkPassable(x,y,z)) {
            do {
                y--;
                distance--;
            } while (!checkPassable(x, y, z));
        } else {
            distance = -1;
            do {
                y--;
                distance++;
            } while(checkPassable(x,y,z));
        }
        return distance;
    }

    public double jumpHeight(Vector velocity) {
        int minX = (int) (min.getX()+velocity.getX());
        int minY= (int) min.getY();
        int minZ = (int) (min.getZ()+velocity.getZ());
        int maxX = (int) (max.getX()+velocity.getX());
        int maxY= (int) max.getY();
        int maxZ = (int) (max.getZ()+velocity.getZ());
        return - Math.min(distanceToGround(minX, minY, minZ), Math.min(distanceToGround(minX, minY, maxZ),
                 Math.min(distanceToGround(maxX, minY, minZ),distanceToGround(maxX, minY, maxZ))))
               -(min.getY() - minY);
    }

    private boolean checkPassable(int x, int y, int z) {
        return EntitiesPlugin.getEntityServer().isPassable(x,y,z);
    }*/

    public static EntityBoundingBox getBoundingBox(McmeEntityType type) {
        if(type.isCustomType()) {
            return new EntityBoundingBox(0.3,0.3,0,2);
        } else {
            switch(type.getBukkitEntityType()) {
                default:
                    return new EntityBoundingBox(0.3, 0.3, 0, 2);
            }
        }
    }

}
