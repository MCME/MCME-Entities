package com.mcmiddleearth.entities.events.events.virtual.composite;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.events.events.McmeEntityEvent;
import com.mcmiddleearth.entities.events.events.virtual.VirtualEntityEvent;

public abstract class BakedAnimationEntityEvent extends VirtualEntityEvent {

    private final BakedAnimationEntity entity;

    public BakedAnimationEntityEvent(BakedAnimationEntity entity) {
        super(entity);
        this.entity = entity;
    }

    public BakedAnimationEntity getBakedAnimationEntity() {
        return entity;
    }

}
