package com.mcmiddleearth.entities.protocol.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.mcmiddleearth.entities.events.events.VirtualEntityEvent;
import com.mcmiddleearth.entities.server.EntityServer;
import org.bukkit.plugin.Plugin;

public class EntityListener extends PacketAdapter {

    protected EntityServer entityServer;

    public EntityListener(Plugin plugin, EntityServer entityServer, PacketType... types) {
        super(plugin, types);
        this.entityServer = entityServer;
    }

    protected void throwEvent(VirtualEntityEvent event) {
        entityServer.handleEvent(event);
    }
}
