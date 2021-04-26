package com.mcmiddleearth.entities.entities.composite;

import org.bukkit.Location;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * Provides data about a composite entity. A composite entity consists of several parts which are displayed in game by armor_stand entities. Creating those armor stands in game is not a responsibility of implementing classes.
 */
public interface CompositeEntityData {

    /**
     * Get the parts this composite entity is composed of.
     * @return Array of the parts.
     */
    public CompositeEntityBoneData[] getParts();

    /**
     * Update all part data to the next server tick (50 ms). This includes calculating new locations of all parts due
     * to a set velocity of the composite entity as well as new locations due to active animation.
     */
    public void doTick();

    /**
     * Set the velocity of the composite entity.
     * @param velocity New velocity in units of blocks per second
     */
    public void setVelocity(Vector velocity);

    /**
     * Set a new location of the composite entity (aka teleportation).
     * @param location New location including yaw and pitch
     */
    public void setLocation(Location location);

    public Location getLocation();

    /**
     * Set yaw angle of the composite entity as a whole.
     * @param yaw new yaw angle in units of degree. 0° means pointing south. Range from -180° to 180°
     */
    public void setYaw(float yaw);

    /**
     * Get current yaw angle of the composite entity as a whole.
     * @return Current yaw angle in units of degree. 0° means pointing south. Range from -180° to 180°n as in Minecraft locations.
     */
    public float getYaw();

    /**
     * Set yaw angle of the composite entity as a whole.
     * @param pitch new pitch angle in units of degree. 0° means pointing horizontally. Range from -90° to 90° as in Minecraft locations.
     */
    public void setPitch(float pitch);

    /**
     * Get current pitch angle of the composite entity as a whole.
     * @return Current pitch angle in units of degree. 0° means pointing horizontally. Range from -90° to 90°n as in Minecraft locations.
     */
    public float getPitch();

    /**
     * Set an animation to play.
     * @param animationId Id of the animation to play
     */
    public void setAnimation(String animationId);

    /**
     * Get the animation currenty playing
     */
    public String getAnimation();

    /**
     * Set pose of a part of the Composite entity. All child parts are also updated.
     * @param partId Id of the part to change.
     * @param pose Euler angle to apply.
     */
    public void setPose(int partId, EulerAngle pose);

}
