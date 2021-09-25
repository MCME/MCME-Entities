package com.mcmiddleearth.entities.events.events.player;

import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.virtual.VirtualEntityEvent;

public abstract class VirtualEntityPlayerEvent extends VirtualEntityEvent {

    private final RealPlayer player;

    public VirtualEntityPlayerEvent(RealPlayer player, VirtualEntity entity) {
        super(entity);
        this.player = player;
    }

    public RealPlayer getPlayer() {
        return player;
    }

}
