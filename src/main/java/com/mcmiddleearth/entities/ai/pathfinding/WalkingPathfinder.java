package com.mcmiddleearth.entities.ai.pathfinding;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.provider.BlockProvider;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class WalkingPathfinder implements Pathfinder{

    private final VirtualEntity entity;

    private final BlockProvider blockProvider;

    private Vector target;

    private int maxPathLength = 50;

    private PathMarker leaveWall, current;
    private int leaveWallIndex = -1;

    private boolean followRightSideWall;

    private Path path;

    boolean fail;
    int step;

    public WalkingPathfinder(VirtualEntity entity) {
        this.entity = entity;
        this.target = getBlockCenterXZ(entity.getLocation().toVector());
        this.blockProvider = EntitiesPlugin.getEntityServer().getBlockProvider(entity.getLocation().getWorld().getUID());
    }

    public void setMaxPathLength(int maxPathLength) {
        this.maxPathLength = maxPathLength;
    }

    private Path getPath(PathMarker start, boolean followRightSideWall) {
//Logger.getGlobal().info("begin recursion step: ");
        path = new Path(target);
        current = new PathMarker(start);
        step = 0;
        fail = false;
        this.followRightSideWall = followRightSideWall;

        //current.setRotation(getTargetDirection(current.getPoint()));
        current.turn(!followRightSideWall);
        followWall(followRightSideWall);
        getPathRecursive();
//Logger.getGlobal().info("finish recursion step: ");
        return path;
    }

    @Override
    public Path getPath(Vector start) {
//Logger.getGlobal().info("**************************findPath start: "+start);
//Logger.getGlobal().info("findPath target: "+target);
        if(target!=null) {
            path = new Path(target);
            current = new PathMarker(0, getBlockCenterXZ(start));
            step = 0;
            fail = false;
            getPathRecursive();
//Logger.getGlobal().info("finished path: ");
//path.getPoints().forEach(vector -> {
//    System.out.println("x: " + vector.getBlockX() + " y: " + vector.getBlockY() + " z: " + vector.getBlockZ());}
//);
            return path;
        } else {
            return null;
        }
    }

    private Path getPathRecursive() {
//Logger.getGlobal().info("find path recursive: "+maxPathLength+" "+followRightSideWall+" start: "+current.getPoint().getBlockX()+" "+current.getPoint().getBlockZ());
//Logger.getGlobal().info("not complete: "+!path.isComplete()+" && not fail "+ !fail +" && step "+(step < maxPathLength));
        while(!path.isComplete() && !fail && step < maxPathLength) {
            if(path.contains(current.getPoint()) && leaveWall != null) {
                path.shortcut(current.getPoint(), leaveWallIndex);
                leaveWallIndex = -1;
                current = new PathMarker(leaveWall);//start = path.get(path.size()-1);
//Logger.getGlobal().info("current after shortcut: "+current.getPoint());
                //this.setLocation(start.x, start.y);
                //this.setRotation(leaveWallRotation);
                // nachricht("rotation: "+this.getRotation()+"   x: "+getX()+" y: "+getY());
                followWall(followRightSideWall);
            }

            current.setRotation(getTargetDirection(current.getPoint()));
//Logger.getGlobal().info("rotation direct: "+current.getRotation());
            PathMarker next = new PathMarker(current);
            moveMarker(next);
            if(canMove(next)) {
                path.addPoint(current.getPoint());
//Logger.getGlobal().info("added direct "+current.getPoint().getBlockX()+" "+current.getPoint().getBlockZ());
//entity.getLocation().getWorld().dropItem(new Location(entity.getLocation().getWorld(),current.getPoint().getX(),
//                current.getPoint().getY(),
//                current.getPoint().getZ()),new ItemStack(Material.STONE));
                step++;
                current.move(next.getPoint().getY());
//Logger.getGlobal().info("current: "+current.getPoint()+" - "+current.getRotation());
            } else {
//Logger.getGlobal().info("follow Wall");
                //followRight = false;
                /* replaced by recursive call
                current.turn(!followRightSideWall);
                followWall(followRightSideWall);*/
/*Logger.getGlobal().info("incomplete Path");
path.getPoints().forEach(vector -> {
    System.out.println("x: " + vector.getBlockX()  + " z: " + vector.getBlockZ());}
);*/
                WalkingPathfinder finder = new WalkingPathfinder(entity);
                finder.setTarget(target.clone());
                finder.setMaxPathLength(maxPathLength-step);
                Path right = finder.getPath(current, true);
                Path left = finder.getPath(current,false);
/*Logger.getGlobal().info("right Path");
right.getPoints().forEach(vector -> {
    System.out.println("x: " + vector.getBlockX()  + " z: " + vector.getBlockZ());}
);
Logger.getGlobal().info("left Path");
left.getPoints().forEach(vector -> {
    System.out.println("x: " + vector.getBlockX()  + " z: " + vector.getBlockZ());}
);*/
//Logger.getGlobal().info("left: "+left.isComplete()+" "+left.length()+" right: "+right.isComplete()+" "+right.length());
                if(right.isComplete() && (!left.isComplete() || right.length()<left.length())) {
                    path.append(right);
                } else {
                    path.append(left);
                }
/*path.getPoints().forEach(vector -> {
    System.out.println("x: " + vector.getBlockX() + " z: " + vector.getBlockZ());}
);*/
                break;
            }
        }
        //if(path.isComplete()) {
//path.getPoints().forEach(vector -> {
//    System.out.println("x: " + vector.getX() + " y: " + vector.getY() + " z: " + vector.getZ());}
//);
            path.optimise(entity.getJumpHeight(), entity.getFallDepth());

            return path;
        //} else {
        //    return null;
        //}
    }

    public void followWall(boolean rightSide) {
        int rotation, oldRotation, val1, val2;
        do {
            path.addPoint(current.getPoint());
//Logger.getGlobal().info("added wall "+current.getPoint().getBlockX()+" "+current.getPoint().getBlockZ());
//entity.getLocation().getWorld().dropItem(new Location(entity.getLocation().getWorld(),current.getPoint().getX(),
//                                                                                      current.getPoint().getY(),
//                                                                                      current.getPoint().getZ()),
//        new ItemStack(Material.STONE));
            step++;
            //PathMarker next = new PathMarker(current);
            //moveMarker(next);
            //current.move(next.getPoint().getY());
            int targetDirection = getTargetDirection(current.getPoint());
            oldRotation = current.getRotation()-targetDirection;
            current.turn(rightSide);
            PathMarker next = new PathMarker(current);
            moveMarker(next);
            int i = 0;
            while(!canMove(next) && i < 4) {
                current.turn(!rightSide);
                next = new PathMarker(current);
                moveMarker(next);
                i++;
            }
            if(i == 4) {
                fail = true;
                return;
            }
            //insertion
            current.move(next.getPoint().getY());
            //insertion end
            rotation = current.getRotation()-targetDirection;
//Logger.getGlobal().info("Target rotation: "+targetDirection+" old: "+oldRotation+" new: "+rotation);
            //nachricht("rotation: "+rotation+" alteM: "+alteMarke);
            if(rightSide) {
                val1 = 90;
                val2 = -270;
            } else {
                val1 = -90;
                val2 = 270;
            }
/*Logger.getGlobal().info("Val1: "+val1 + " val2: "+val2);
Logger.getGlobal().info("step: "+(step < maxPathLength) + "not complete: "+ !path.isComplete()
                + "&& !(("+ (rotation == val1) +" || " +(rotation== val2)
                +" || "+ (oldRotation == val1)+" || "+(oldRotation== val2)
                +" && not contains "+path.contains(current.getPoint()));*/
        } while(step < maxPathLength && !path.isComplete()
                && !(( rotation == val1 || rotation== val2 || oldRotation == val1 || oldRotation== val2)
                      && !path.contains(current.getPoint())));
        leaveWallIndex = path.length()-1; //one too early?
        leaveWall = new PathMarker(current);
    }

    public int getTargetDirection(Vector start) {
        Vector diff = target.clone().subtract(start);
        if(Math.abs(diff.getX())>Math.abs(diff.getZ())) {
            if(diff.getX()>0) {
                return 0;
            } else {
                return 180;
            }
        } else {
            if(diff.getZ()>0) {
                return 90;
            } else {
                return 270;
            }
        }
    }

    private void moveMarker(PathMarker next) {
        next.move(next.getPoint().getY());
        double blockY = blockProvider.blockTopY(next.getPoint().getBlockX(),next.getPoint().getBlockY(),next.getPoint().getBlockZ(),
                                                entity.getJumpHeight()+1);
        next.getPoint().setY(blockY);
    }

    private boolean canMove(PathMarker next) {
//Logger.getGlobal().info("CanMove: "+current.getPoint().getBlockX()+" "+current.getPoint().getBlockZ()+" "
//                          +next.getPoint().getBlockX()+" "+next.getPoint().getBlockZ()+" "
//                          +current.getPoint().getY() +" - "+next.getPoint().getY());
        return next.getPoint().getY() - current.getPoint().getY() <= entity.getJumpHeight()
                && current.getPoint().getY() - next.getPoint().getY() <= entity.getFallDepth();
    }

    @Override
    public void setTarget(Vector target) {
        this.target = target;
    }

    @Override
    public Vector getTarget() {
        return target;
    }

    private Vector getBlockCenterXZ(Vector vector) {
        return new Vector(vector.getBlockX()+0.5,vector.getY(), vector.getBlockZ()+0.5);
    }

    public static class PathMarker {

        public static final BlockFace[] DIRECTIONS = new BlockFace[]{BlockFace.EAST,BlockFace.SOUTH,BlockFace.WEST,BlockFace.NORTH};

        private int directionIndex;
        private BlockFace direction;

        private int rotation;
        private final Vector point;

        public PathMarker(int rotation, Vector point) {
            this.rotation = rotation;
            this.point = new Vector(point.getX(), point.getY(), point.getZ());
        }

        public PathMarker(PathMarker marker) {
            this.rotation = marker.rotation;
            this.point = marker.getPoint().clone();
            this.directionIndex = marker.directionIndex;
            this.direction = marker.direction;
        }

        public BlockFace getDirection() {
            return direction;
        }

        public int getRotation() {
            return rotation;
        }

        public void setRotation(int rotation) {
            this.rotation = rotation;
        }

        public Vector getPoint() {
            return point;
        }

        public void turn(boolean right) {
            int angle = (right?90:-90);
            rotation = rotation + angle;
            while(rotation<0) {
                rotation+=360;
            }
            while(rotation>=360) {
                rotation -=360;
            }
        }

        public void uTurn() {
            rotation = rotation + 180;
            while(rotation>=360) {
                rotation -=360;
            }
        }

        public void move(double nextY) {
            switch(rotation) {
                case 0: point.add(new Vector(1,0,0));
                    break;
                case 90: point.add(new Vector(0,0,1));
                    break;
                case 180: point.add(new Vector(-1,0,0));
                    break;
                default: point.add(new Vector(0,0,-1));
                    break;
            }
            point.setY(nextY);
        }

        public String toString() {
            return "x: "+point.getX()+" y: "+point.getY()+" z: "+point.getZ();
        }
    }
}
