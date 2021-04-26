package com.mcmiddleearth.entities.provider;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;

public interface BlockProvider {

    public BlockData getBlockDataAt(Location location);

    public BoundingBox getBoundingBox(int x, int y, int z);

    public boolean isPassable(int x, int y, int z);

    public double blockTopY(int x, int y, int z, int range);

}
