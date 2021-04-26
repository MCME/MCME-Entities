package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class VirtualEntityDestroyPacket extends AbstractPacket {

    PacketContainer destroy;

    public VirtualEntityDestroyPacket(int... entityId) {
        destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntegerArrays().write(0, entityId);
    }

    @Override
    public void send(Player recipient) {
//Logger.getGlobal().info("send destroy to : "+recipient.getName());
        send(destroy, recipient);
    }
}
