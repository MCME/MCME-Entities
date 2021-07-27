package com.mcmiddleearth.entities.events.events.player;

import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.Cancelable;

public class VirtualPlayerAttackEvent extends VirtualEntityPlayerEvent implements Cancelable {

    private final boolean isSneaking;

    private boolean isCancelled;

    public VirtualPlayerAttackEvent(RealPlayer player, VirtualEntity entity, boolean isSneaking) {
        super(player, entity);
        this.isSneaking = isSneaking;
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
