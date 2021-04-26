package com.mcmiddleearth.entities.ai.pathfinding;

import org.bukkit.util.Vector;

public interface Pathfinder {

    Path getPath(Vector start);

    void setTarget(Vector target);

    Vector getTarget();

}
