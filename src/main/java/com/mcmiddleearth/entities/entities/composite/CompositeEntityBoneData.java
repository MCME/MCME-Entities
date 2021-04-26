package com.mcmiddleearth.entities.entities.composite;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.EulerAngle;

/**
 * Contains data about a part of a composite entity. These data can be used by other plugins to display this part in game using armor stands.
 */
public interface CompositeEntityBoneData {

    /**
     * Get the location of an armor_stand entity to display this part in game.
     * @return armor stand location
     */
    public Location getLocation();

    /**
     * Get the head pose of an armor_stand entity to display this part in game.
     * @return armor stand head pose
     */
    public EulerAngle getHeadPose();

    /**
     * Get the material of the item to set as helmet of an armor stand to display this part in game
     * @return helmet material
     */
    public Material getHeadMaterial();

    /**
     * Set a new material used to display this part in game
     * @param material new helmet material
     */
    public void setHeadMaterial(Material material);

    /**
     * Get the damage metadata value of the helmet item to display this part in game
     * @return helmet item damage metadata
     */
    public int getDamage();

    /**
     * Set new helmet damage metadata
     * @param Damage new helmet damage metadata
     */
    public void setDamage(int Damage);

    /**
     * Get if this part should be visible in game
     * @return visibility in game
     */
    public boolean isVisible();

    /**
     * set if this part should be visible in game
     * @param visible visibility in game
     */
    public void setVisible(boolean visible);

}
