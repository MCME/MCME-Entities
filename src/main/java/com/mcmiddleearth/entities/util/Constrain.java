package com.mcmiddleearth.entities.util;

import com.mcmiddleearth.entities.exception.InvalidLocationException;
import org.bukkit.Location;
import org.bukkit.World;

public class Constrain {

    public static void checkSameWorld(Location location, World expected) throws InvalidLocationException {
        if(!location.getWorld().equals(expected)) {
            throw new InvalidLocationException("Expected location in world "+expected.getName()+" but found world "+location.getWorld().getName()+".",location.getWorld(),expected);
        }
    }
    public static void checkSameWorld(Location[] locations, World expected) throws InvalidLocationException {
        for(Location loc:locations) {
            if (!loc.getWorld().equals(expected)) {
                throw new InvalidLocationException("Expected all locations in world " + expected.getName() + " but found world " + loc.getWorld().getName() + ".", loc.getWorld(), expected);
            }
        }
    }
}
