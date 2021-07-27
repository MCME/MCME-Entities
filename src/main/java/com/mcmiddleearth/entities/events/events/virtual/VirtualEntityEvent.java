package com.mcmiddleearth.entities.events.events.virtual;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.McmeEntityEvent;

public abstract class VirtualEntityEvent implements McmeEntityEvent {

    VirtualEntity entity;

    public VirtualEntityEvent(VirtualEntity entity) {
        this.entity = entity;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }

    public VirtualEntity getVirtualEntity() { return entity;}

}
