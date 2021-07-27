package com.mcmiddleearth.entities.events.events.player;

import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.Cancelable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

public class VirtualPlayerInteractAtEvent extends VirtualPlayerInteractEvent implements Cancelable {

    private final Vector interactPoint;

    private boolean isCancelled;

    public VirtualPlayerInteractAtEvent(RealPlayer player, VirtualEntity entity, Vector vector, EquipmentSlot hand, boolean isSneaking) {
        super(player, entity, hand, isSneaking);
        this.interactPoint = vector;
    }

    public Vector getInteractPoint() {
        return interactPoint;
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
