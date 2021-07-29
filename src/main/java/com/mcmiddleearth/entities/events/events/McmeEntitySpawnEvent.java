package com.mcmiddleearth.entities.events.events;

import com.mcmiddleearth.entities.entities.McmeEntity;

public class McmeEntitySpawnEvent implements McmeEntityEvent {

    McmeEntity entity;

    public McmeEntitySpawnEvent(McmeEntity entity) {
        this.entity = entity;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }
}
