package com.mcmiddleearth.entities.provider;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;

public interface BlockProvider {

    BlockData getBlockDataAt(Location location);

    BoundingBox getBoundingBox(int x, int y, int z);

    boolean isPassable(int x, int y, int z);

    double blockTopY(int x, int y, int z, int range);

}
