package com.mcmiddleearth.entities.ai.pathfinding;

import org.bukkit.util.Vector;

public class SimplePathfinder implements Pathfinder {

    private Vector target;
    @Override
    public Path getPath(Vector start) {
        Path result = new Path(target);
        result.addPoint(start);
        result.addPoint(target);
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
}
