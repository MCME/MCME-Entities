package com.mcmiddleearth.entities.events.events;

import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

public class VirtualPlayerInteractAtEvent extends VirtualPlayerInteractEvent {

    private final Vector interactPoint;

    public VirtualPlayerInteractAtEvent(RealPlayer player, VirtualEntity entity, Vector vector, EquipmentSlot hand, boolean isSneaking) {
        super(player, entity, hand, isSneaking);
        this.interactPoint = vector;
    }

    public Vector getInteractPoint() {
        return interactPoint;
    }
}
