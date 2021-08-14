package com.mcmiddleearth.entities.events.events.virtual;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.Cancelable;

public class VirtualEntityAttackEvent extends VirtualEntityEvent implements Cancelable {

    private final McmeEntity target;

    private boolean isCancelled;

    public VirtualEntityAttackEvent(VirtualEntity entity, McmeEntity target) {
        super(entity);
        this.target = target;
    }

    public McmeEntity getTarget() {
        return target;
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
