package com.mcmiddleearth.entities.events;

public interface Cancelable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
