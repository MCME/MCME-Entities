package com.mcmiddleearth.entities.events.handler;

import com.mcmiddleearth.entities.events.events.McmeEntityEvent;
import com.mcmiddleearth.entities.events.listener.McmeEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class McmeEntityEventHandler {

    private final Method handler;

    private final McmeEventListener listener;

    public McmeEntityEventHandler(Method handler, McmeEventListener listener) {
        this.handler = handler;
        this.listener = listener;
    }

    public void handle(McmeEntityEvent event) {
        try {
            handler.invoke(listener, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McmeEntityEventHandler that = (McmeEntityEventHandler) o;
        return handler.equals(that.handler) &&
                listener.equals(that.listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handler, listener);
    }
}
