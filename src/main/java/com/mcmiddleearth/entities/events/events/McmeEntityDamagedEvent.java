package com.mcmiddleearth.entities.events.events;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.events.Cancelable;

public class McmeEntityDamagedEvent implements McmeEntityEvent, Cancelable {

    private McmeEntity entity;

    private int damage;

    private boolean isCancelled;

    public McmeEntityDamagedEvent(McmeEntity entity, int damage) {
        this.entity = entity;
        this.damage = damage;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
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
