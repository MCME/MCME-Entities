package com.mcmiddleearth.entities.events.events;

import com.mcmiddleearth.entities.entities.McmeEntity;

public class McmeEntityRemoveEvent implements McmeEntityEvent {

    McmeEntity entity;

    public McmeEntityRemoveEvent(McmeEntity entity) {
        this.entity = entity;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }
}
