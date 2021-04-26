package com.mcmiddleearth.entities.protocol.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mcmiddleearth.entities.EntityAPI;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.events.events.VirtualPlayerAttackEvent;
import com.mcmiddleearth.entities.events.events.VirtualPlayerInteractAtEvent;
import com.mcmiddleearth.entities.events.events.VirtualPlayerInteractEvent;
import com.mcmiddleearth.entities.server.EntityServer;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class VirtualEntityUseListener extends EntityListener {

    public VirtualEntityUseListener(Plugin plugin, EntityServer entityServer) {
        super(plugin, entityServer, PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        RealPlayer player = EntityAPI.getOrCreateMcmePlayer(event.getPlayer());
        int entityId = packet.getIntegers().read(0);
        McmeEntity entity = entityServer.getEntity(entityId);
        if(entity instanceof VirtualEntity) {
            event.setCancelled(true);
            EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
            EquipmentSlot hand;
            if(!action.equals(EnumWrappers.EntityUseAction.ATTACK)
                    && packet.getHands().read(0).equals(EnumWrappers.Hand.MAIN_HAND)) {
                hand = EquipmentSlot.HAND;
            } else {
                hand = EquipmentSlot.OFF_HAND;
            }
            boolean isSneaking = packet.getBooleans().read(0);
            switch(action) {
                case INTERACT_AT:
//Logger.getGlobal().info("interact at!");
                    Vector vector = packet.getVectors().read(0);
//Logger.getLogger(this.getClass().getName()).info("X: " + vector.getX());
//Logger.getLogger(this.getClass().getName()).info("Y: " + vector.getY());
//Logger.getLogger(this.getClass().getName()).info("Z: " + vector.getZ());
                    throwEvent(new VirtualPlayerInteractAtEvent(player, (VirtualEntity)entity, vector, hand, isSneaking));
                    break;
                case INTERACT:
//Logger.getGlobal().info("interact!");
                    throwEvent(new VirtualPlayerInteractEvent(player, (VirtualEntity)entity, hand, isSneaking));
                    break;
                case ATTACK:
//Logger.getGlobal().info("attack!");
                    throwEvent(new VirtualPlayerAttackEvent(player, (VirtualEntity)entity, isSneaking));
                    break;
            }
        }
    }
}
