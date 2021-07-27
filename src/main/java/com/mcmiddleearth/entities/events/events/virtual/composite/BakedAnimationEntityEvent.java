package com.mcmiddleearth.entities.events.events.virtual.composite;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.events.events.McmeEntityEvent;

public abstract class BakedAnimationEntityEvent implements McmeEntityEvent {

    private final BakedAnimationEntity entity;

    public BakedAnimationEntityEvent(BakedAnimationEntity entity) {
        this.entity = entity;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }

    public BakedAnimationEntity getBakedAnimationEntity() {
        return entity;
    }

}
