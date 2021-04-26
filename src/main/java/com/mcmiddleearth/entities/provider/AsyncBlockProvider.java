package com.mcmiddleearth.entities.provider;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;

public class AsyncBlockProvider implements BlockProvider {

    @Override
    public BlockData getBlockDataAt(Location location) {
        return null;
    }

    @Override
    public BoundingBox getBoundingBox(int x, int y, int z) {
        return null;
    }

    @Override
    public boolean isPassable(int x, int y, int z) {
        return false;
    }

    @Override
    public double blockTopY(int x, int y, int z, int range) {
        return 0;
    }
}
