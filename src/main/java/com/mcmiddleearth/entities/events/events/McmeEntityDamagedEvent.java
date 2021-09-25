package com.mcmiddleearth.entities.events.events;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.events.Cancelable;

public class McmeEntityDamagedEvent implements McmeEntityEvent, Cancelable {

    private final McmeEntity entity;

    private double damage;

    private boolean isCancelled;

    public McmeEntityDamagedEvent(McmeEntity entity, double damage) {
        this.entity = entity;
        this.damage = damage;
    }

    @Override
    public McmeEntity getEntity() {
        return entity;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
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
