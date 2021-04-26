package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.EntitiesPlugin;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractPacket {

    protected ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    protected void send(PacketContainer packet, Set<Player> recipients) {
        recipients.forEach(player -> send(packet, player));
    }

    protected void send(PacketContainer packet, Player recipient) {
        try {
            protocolManager.sendServerPacket(recipient, packet);
        } catch (InvocationTargetException e) {
            Logger.getLogger(EntitiesPlugin.class.getSimpleName()).log(Level.WARNING,"Error while sending Packet!",e);
        }
    }

    public void send(Set<Player> recipients) {
        recipients.forEach(this::send);
    }

    public abstract void send(Player recipient);

    public void update() {}
}
