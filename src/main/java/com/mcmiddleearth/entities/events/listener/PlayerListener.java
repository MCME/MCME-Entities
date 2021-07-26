package com.mcmiddleearth.entities.events.listener;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.EntityAPI;
import com.mcmiddleearth.entities.ai.goal.GoalDistance;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.events.events.VirtualPlayerAttackEvent;
import com.mcmiddleearth.entities.events.handler.EntityEventHandler;
import com.mcmiddleearth.entities.protocol.listener.VirtualEntityUseListener;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class PlayerListener implements Listener, McmeEventListener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RealPlayer player = EntityAPI.getMcmePlayer(event.getPlayer().getUniqueId());
        if(player != null) {
            EntitiesPlugin.getEntityServer().getPlayerProvider().removePlayer(event.getPlayer());
        }
    }

    @EntityEventHandler
    public void attackVirtualEntity(VirtualPlayerAttackEvent event) {
        if(event.getPlayer().getLocation().distanceSquared(event.getEntity().getLocation()) < GoalDistance.ATTACK*1.5) {
            int damage = (int) (Math.random() * 8);
//Logger.getGlobal().info("Attack: " + event.getEntity().getType().getBukkitEntityType() + " " + damage);
            event.getEntity().receiveAttack(event.getPlayer(), damage, 1);
        }
    }

    /*@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        //manager.addPacketListener(new EntityListener(this));
        Logger.getGlobal().info("Manager: "+manager);
        manager.addPacketListener(new VirtualEntityUseListener(EntitiesPlugin.getInstance(), EntitiesPlugin.getEntityServer()));

    }*/
}
