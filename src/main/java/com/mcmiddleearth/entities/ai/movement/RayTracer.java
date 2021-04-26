package com.mcmiddleearth.entities.ai.movement;

import com.mcmiddleearth.entities.util.TriFunction;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RayTracer<T> {

    private final List<Ray> rays = new ArrayList<>();

    private RayTraceResultColumn current;

    private RayTraceResultColumn next;

    private final int stepX, stepZ;
    private int blockX, lastStartZ, blockStartX;
    private final int blockEndX;

    private boolean expandStartX = false;

    private final Vector start;
    private final Vector traceVector;

    private final double mz, myx, myz;

    TriFunction<Integer, Integer, Integer, T> calculator;

    public RayTracer(Vector start, Vector traceVector, TriFunction<Integer, Integer, Integer, T> calculator) {
        this.calculator = calculator;
        this.start = start.clone();
        this.traceVector = traceVector;
        stepX = (traceVector.getX()>0?1:-1);
        stepZ = (traceVector.getZ()>0?1:-1);
        mz = traceVector.getZ() / traceVector.getX();
        myx = traceVector.getY() / traceVector.getX();
        myz = traceVector.getY() / (traceVector.getZ()+traceVector.getX()*traceVector.getX()/traceVector.getZ());
//Logger.getGlobal().info("mZ:"+mz+" myx:"+myx+" myz:"+myz);
        blockStartX = start.getBlockX();
        blockEndX = getBlock(start.getX()+traceVector.getX());
        blockX = blockStartX;
    }

    public void initTrace() {
        int rayStartX = getRayStartX();
        if(rayStartX != blockStartX) {
            blockStartX = rayStartX;
            blockX = blockStartX;
            expandStartX = true;
        }
    }

    public void traceStep() {
        if(current == null) {
            current = createResultColumn();
        } else {
            current = next;
        }
        if(blockX != blockEndX) {
            blockX += stepX;
            next = createResultColumn();
        } else {
            blockX += stepX;
            next = null;
        }
        //result = new RayTraceResultColumn[blockEndX- blockX +1];
        //while(blockX != blockEndX) {
    }

    private RayTraceResultColumn createResultColumn() {
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int maxY = 1;
        for (Ray ray : rays) {
            int rayX = (stepX>0?blockX+1:blockX);
            int rayY = getBlock(ray.getY(rayX));
            int rayZ = getBlock(ray.getZ(rayX));
//Logger.getGlobal().info("rayY: "+ray.getY(rayX)+" "+rayY);
            if (rayY > maxY) {
                maxY = rayY;
            }
            if (rayZ > maxZ) {
                maxZ = rayZ;
            }
            if (rayZ < minZ) {
                minZ = rayZ;
            }
        }
        int startZ, lengthZ;
        if(stepZ>0) {
            startZ = Math.min(minZ, lastStartZ);
            if(blockX == blockStartX) {
                if(expandStartX) {
                    startZ = start.getBlockZ();
                } else {
                    startZ = getRayMinStartZ();
                }
            }
            if(blockX == blockEndX) {
                maxZ = getBlock(start.getZ()+traceVector.getZ());
            }
            lengthZ = maxZ - startZ + 1;
        } else {
            startZ = Math.max(maxZ, lastStartZ);
            if(blockX == blockStartX) {
                if(expandStartX) {
                    startZ = start.getBlockZ();
                } else {
                    startZ = getRayMaxStartZ();
                }
            }
            if(blockX == blockEndX) {
                minZ = getBlock(start.getZ()+traceVector.getZ());
            }
            lengthZ = startZ - minZ +1;
        }
        lastStartZ = startZ;
//Logger.getGlobal().info("blockStartX:"+blockStartX+" blockX:"+blockX+" blockEndX:"+blockEndX);
//Logger.getGlobal().info("minZ:"+minZ+" maxZ"+maxZ+" startZ:"+startZ+" lenngthZ:"+lengthZ);
        int[] blockYs = new int[Math.max(lengthZ,1)];
        for(int i = 0; i < blockYs.length; i++) {
            blockYs[i] = maxY + getBlock(myz * i);
        }
        return new RayTraceResultColumn(blockX, startZ, blockYs);
    }

    public void addRay(Vector start) {
        rays.add(new Ray(start));
    }

    public int first() {
        return blockStartX;
    }

    public int last() {
        return blockEndX;//start.getBlockX()+ stepX*(rays.size()-1);
    }

    public int stepX() {
        return stepX;
    }

    public int stepZ() {
        return stepZ;
    }

    /*public RayTraceResultColumn get(int i) {
        return result[(i-start.getBlockX())*stepX];
    }*/

    public RayTraceResultColumn current() {
        return current;
    }

    public RayTraceResultColumn next() {
        return next;
    }

    private T calculateValue(int x, int y, int z) {
        return calculator.apply(x,y,z);
    }

    private int getBlock(double cord) {
        return (int) Math.floor(cord);
    }

    private int getRayStartX() {
        int rayStart;
        if(stepX > 0) {
            rayStart = Integer.MAX_VALUE;
            for(Ray ray: rays) {
                if (ray.getStartX() < rayStart) {
                    rayStart = ray.getStartX();
                }
            }
        } else {
            rayStart = Integer.MIN_VALUE;
            for(Ray ray: rays) {
                if (ray.getStartX() > rayStart) {
                    rayStart = ray.getStartX();
                }
            }
        }
        return rayStart;
    }

    private int getRayMinStartZ() {
        int rayStart = Integer.MAX_VALUE;
        for (Ray ray : rays) {
            if (ray.getStartZ() < rayStart) {
                rayStart = ray.getStartZ();
            }
        }
//Logger.getGlobal().info("RayMinStartZ: "+rayStart);
        return rayStart;
    }

    private int getRayMaxStartZ() {
        int rayStart = Integer.MIN_VALUE;
        for (Ray ray : rays) {
            if (ray.getStartZ() > rayStart) {
                rayStart = ray.getStartZ();
            }
        }
//Logger.getGlobal().info("RayMaxStartZ: "+rayStart);
        return rayStart;
    }

    public class RayTraceResultColumn {

        private final int blockX;

        private final int startZ;

        private final List<T> rows = new ArrayList<>();

        private final int[] blockYs;

        public RayTraceResultColumn(int blockX, int startZ, int[] blockYs) {
            this.blockYs = blockYs;
            for(int ignored : blockYs) this.rows.add(null);
            this.startZ = startZ;
            this.blockX = blockX;
        }

        public int first() {
            return startZ;
        }

        public int last() {
            return startZ + stepZ * (blockYs.length-1);
        }

        public int getBlockX() { return blockX; }

        public T get(int j) {
            int index = (j-startZ)*stepZ;
            if (rows.get(index)==null) {
                rows.set(index, calculateValue(blockX,blockYs[index],j));
            }
            return rows.get(index);
        }

        public T getNext(int j) {
            int index = (j - startZ) * stepZ + 1;
            if (rows.get(index)==null) {
                rows.set(index, calculateValue(blockX,blockYs[index],j+stepZ));
            }
            return rows.get(index);
        }

        public boolean has(int j) {
            int index = (j-startZ)*stepZ;
            return index >= 0 && index < blockYs.length;
        }

        public boolean hasNext(int j) {
            int index = (j-startZ)*stepZ + 1;
            return index >= 0 && index < blockYs.length;
        }
    }

    public class Ray {

        Vector start;

        public Ray(Vector start) {
            this.start = start;
        }

        public double getY(double x) {
            return start.getY()+ myx * (x - start.getX());
        }

        public double getZ(double x) {
//Logger.getGlobal().info("mz: "+mz+" x: "+x+" startX: "+start.getX()+" startZ: "+start.getZ()+" result: "+(start.getZ()+mz * (x - start.getX())));
            return start.getZ()+mz * (x - start.getX());
        }

        public int getStartX() {
            return start.getBlockX();
        }

        public int getStartZ() {
            return start.getBlockZ();
        }
    }

}
