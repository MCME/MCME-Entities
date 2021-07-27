package com.mcmiddleearth.entities.events.events.virtual.composite;

import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.events.Cancelable;

public class BakedAnimationEntityAnimationChangedEvent extends BakedAnimationEntityEvent implements Cancelable {

    private String nextAnimationKey;

    private boolean isCancelled;

    public BakedAnimationEntityAnimationChangedEvent(BakedAnimationEntity entity, String nextAnimationKey) {
        super(entity);
        this.nextAnimationKey = nextAnimationKey;
    }

    public String getNextAnimationKey() {
        return nextAnimationKey;
    }

    public void setNextAnimationKey(String nextAnimationKey) {
        this.nextAnimationKey = nextAnimationKey;
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
