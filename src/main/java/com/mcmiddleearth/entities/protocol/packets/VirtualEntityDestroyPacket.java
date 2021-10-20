package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.logging.Logger;

public class VirtualEntityDestroyPacket extends AbstractPacket {

    PacketContainer destroy;

    public VirtualEntityDestroyPacket(int... entityId) {
        destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntegerArrays().write(0, entityId);
//Logger.getGlobal().info("create destroy to : ");
//Arrays.stream(entityId).forEach(eid -> Logger.getGlobal().info("eid: "+eid));
    }

    @Override
    public void send(Player recipient) {
//Logger.getGlobal().info("send destroy to : "+recipient.getName()+destroy.getIntegerArrays().read(0).length);
//for( int i = 0; i< destroy.getIntegerArrays().read(0).length;i++) {
//    Logger.getGlobal().info("eid: "+destroy.getIntegerArrays().read(0)[i]);
//}
        send(destroy, recipient);
    }
}
