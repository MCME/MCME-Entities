package com.mcmiddleearth.entities.events.events.virtual.composite;

import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.events.Cancelable;

public class BakedAnimationEntityStateChangedEvent extends BakedAnimationEntityEvent implements Cancelable {

    private String nextState;

    private boolean isCancelled;

    public BakedAnimationEntityStateChangedEvent(BakedAnimationEntity entity, String nextState) {
        super(entity);
        this.nextState = nextState;
    }

    public String getNextState() {
        return nextState;
    }

    public void setNextState(String nextState) {
        this.nextState = nextState;
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
