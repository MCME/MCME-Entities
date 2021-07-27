package com.mcmiddleearth.entities.events.handler;

import com.mcmiddleearth.entities.events.events.McmeEntityEvent;
import com.mcmiddleearth.entities.events.listener.McmeEventListener;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class McmeEntityEventHandler {

    private final Method handler;

    private final McmeEventListener listener;

    private final Plugin plugin;

    private final EventPriority priority;
    private final boolean ignoreCancelled;

    public McmeEntityEventHandler(Plugin plugin, Method handler, McmeEventListener listener,
                                  EventPriority priority, boolean ignoreCancelled) {
        this.handler = handler;
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.ignoreCancelled = ignoreCancelled;
    }

    public void handle(McmeEntityEvent event) {
        try {
            handler.invoke(listener, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public boolean isIgnoreCancelled() {
        return ignoreCancelled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McmeEntityEventHandler that = (McmeEntityEventHandler) o;
        return plugin.equals(that.plugin)
                && handler.equals(that.handler)
                && listener.equals(that.listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handler, listener);
    }
}
