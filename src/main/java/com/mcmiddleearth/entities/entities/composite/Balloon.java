package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Balloon extends Bone{

    private final Player viewer;

    private float yaw;

    public Balloon(String name, CompositeEntity parent, EulerAngle headPose, Vector relativePosition, ItemStack headItem, Player viewer) {
        super(name, parent, headPose, relativePosition, headItem, false, 0);
        this.viewer = viewer;
    }

    @Override
    public void move() {
        setYaw();
        super.move();
    }

    @Override
    public void teleport() {
        setYaw();
        super.teleport();
    }

    private void setYaw() {
        yaw = viewer.getLocation().getYaw()+180;
        while(yaw>360) {
            yaw -= 360;
        }
    }

    @Override
    public Location getLocation() {
        Location result = parent.getLocation().clone();
        result.setYaw(yaw);
        return result.add(relativePositionRotated);
    }
}
