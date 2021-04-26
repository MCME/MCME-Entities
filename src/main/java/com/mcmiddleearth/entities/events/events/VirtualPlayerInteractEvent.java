package com.mcmiddleearth.entities.events.events;

import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.inventory.EquipmentSlot;

public class VirtualPlayerInteractEvent extends VirtualEntityEvent {

    private final EquipmentSlot hand;

    private final boolean isSneaking;

    public VirtualPlayerInteractEvent(RealPlayer player, VirtualEntity entity, EquipmentSlot hand, boolean isSneaking) {
        super(player, entity);
        this.hand = hand;
        this.isSneaking = isSneaking;
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public boolean isSneaking() {
        return isSneaking;
    }
}
