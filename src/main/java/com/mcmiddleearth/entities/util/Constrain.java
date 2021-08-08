package com.mcmiddleearth.entities.util;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import org.bukkit.Location;
import org.bukkit.World;

public class Constrain {

    public static void checkSameWorld(Location location, World expected) throws InvalidLocationException {
        if(location == null) {
            throw new InvalidLocationException("Expected location is null!", null, expected);
        }
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

    public static void checkEntity(McmeEntity targetEntity) throws InvalidDataException {
        if(targetEntity == null) {
            throw new InvalidDataException("Entity required!");
        }
    }

    public static void checkTargetLocation(Location targetLocation) throws InvalidDataException {
        if(targetLocation == null) {
            throw new InvalidDataException("Target location required!");
        }
    }

    public static void checkCheckpoints(Location[] checkpoints) throws InvalidDataException {
        if(checkpoints == null || checkpoints.length == 0) {
            throw new InvalidDataException("At least one checkpoint is required!");
        }
    }
}
