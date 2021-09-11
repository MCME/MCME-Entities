package com.mcmiddleearth.entities.events.events.virtual;

import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;

public class VirtualEntityTalkEvent extends VirtualEntityEvent {

    private final SpeechBalloonLayout layout;

    private boolean cancelled = false;

    public VirtualEntityTalkEvent(VirtualEntity entity, SpeechBalloonLayout layout) {
        super(entity);
        this.layout = layout;
    }

    public SpeechBalloonLayout getLayout() {
        return layout;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
