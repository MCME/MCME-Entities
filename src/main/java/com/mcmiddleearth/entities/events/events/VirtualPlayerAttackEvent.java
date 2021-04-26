package com.mcmiddleearth.entities.events.events;

import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public class VirtualPlayerAttackEvent extends VirtualEntityEvent {

    private final boolean isSneaking;

    public VirtualPlayerAttackEvent(RealPlayer player, VirtualEntity entity, boolean isSneaking) {
        super(player, entity);
        this.isSneaking = isSneaking;
    }

    public boolean isSneaking() {
        return isSneaking;
    }
}
