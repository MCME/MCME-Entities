package com.mcmiddleearth.entities.events.events;

import com.mcmiddleearth.entities.entities.McmeEntity;

public class McmeEntityDeathEvent implements McmeEntityEvent{

    McmeEntity entity;

    public McmeEntityDeathEvent(McmeEntity entity) {
        this.entity = entity;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }
}
