package com.mcmiddleearth.entities.inventory;

import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public interface McmeInventory extends Inventory, EntityEquipment {

    @Override
    @Nullable McmeEntity getHolder();

}
