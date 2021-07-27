package com.mcmiddleearth.entities.events.events.player;

import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.Cancelable;
import org.bukkit.inventory.EquipmentSlot;

public class VirtualPlayerInteractEvent extends VirtualEntityPlayerEvent implements Cancelable {

    private final EquipmentSlot hand;

    private final boolean isSneaking;

    private boolean isCancelled;

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

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
