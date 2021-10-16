package com.mcmiddleearth.entities.events.events.virtual.composite;

import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.entities.composite.animation.BakedAnimation;
import com.mcmiddleearth.entities.events.Cancelable;

public class BakedAnimationEntityAnimationChangeEvent extends BakedAnimationEntityEvent implements Cancelable {

    private final BakedAnimation current;
    private final BakedAnimation next;
    private final boolean manualAnimationControl, instantAnimationSwitching;
    private boolean cancelled = false;

    public BakedAnimationEntityAnimationChangeEvent(BakedAnimationEntity entity,
                                                    BakedAnimation current,
                                                    BakedAnimation next,
                                                    boolean manualAnimationControl,
                                                    boolean instantAnimationSwitching) {
        super(entity);
        this.current = current;
        this.next = next;
        this.manualAnimationControl = manualAnimationControl;
        this.instantAnimationSwitching = instantAnimationSwitching;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getCurrentAnimation() {
        if(current != null)
            return current.getName();
        else
            return null;
    }

    public String getNextAnimation() {
        if(next != null ) {
            return next.getName();
        } else {
            return null;
        }
    }

    public boolean getManualAnimationControl() {
        return manualAnimationControl;
    }

    public boolean isInstantAnimationSwitching() {
        return instantAnimationSwitching;
    }
}
