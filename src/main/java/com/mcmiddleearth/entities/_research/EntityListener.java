package com.mcmiddleearth.entities._research;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class EntityListener extends PacketAdapter {

    public EntityListener(Plugin plugin) {
        super(plugin, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.ENTITY_ACTION, PacketType.Play.Client.ENTITY_NBT_QUERY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Logger.getLogger(this.getClass().getName()).info(packet.getType().toString());
        Logger.getLogger(this.getClass().getName()).info("Entity ID: "+packet.getIntegers().read(0));
        EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
        Logger.getLogger(this.getClass().getName()).info("Action: "+action.name());
        if(action.equals(EnumWrappers.EntityUseAction.INTERACT_AT)) {
            Vector vector = packet.getVectors().read(0);
            Logger.getLogger(this.getClass().getName()).info("X: " + vector.getX());
            Logger.getLogger(this.getClass().getName()).info("Y: " + vector.getY());
            Logger.getLogger(this.getClass().getName()).info("Z: " + vector.getZ());
        }
        EnumWrappers.Hand hand = packet.getHands().read(0);
        Logger.getLogger(this.getClass().getName()).info("Hand: "+hand.name());
        Logger.getLogger(this.getClass().getName()).info("Sneaking: "+packet.getBooleans().read(0));

    }
}
