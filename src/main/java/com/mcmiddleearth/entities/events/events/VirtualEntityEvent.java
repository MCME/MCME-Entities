package com.mcmiddleearth.entities.events.events;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;

public abstract class VirtualEntityEvent implements McmeEntityEvent {

    VirtualEntity entity;

    RealPlayer player;

    public VirtualEntityEvent(RealPlayer player, VirtualEntity entity) {
        this.entity = entity;
        this.player = player;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }

    public VirtualEntity getVirtualEntity() { return entity;}

    public RealPlayer getPlayer() {
        return player;
    }
}
