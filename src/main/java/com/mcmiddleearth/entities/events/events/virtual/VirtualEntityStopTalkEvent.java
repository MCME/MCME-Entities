package com.mcmiddleearth.entities.events.events.virtual;

import com.mcmiddleearth.entities.entities.VirtualEntity;

public class VirtualEntityStopTalkEvent extends VirtualEntityEvent {

    private boolean cancelled;

    public VirtualEntityStopTalkEvent(VirtualEntity entity) {
        super(entity);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
