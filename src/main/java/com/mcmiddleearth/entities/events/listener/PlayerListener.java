package com.mcmiddleearth.entities.events.listener;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.EntityAPI;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.protocol.listener.VirtualEntityUseListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RealPlayer player = EntityAPI.getMcmePlayer(event.getPlayer().getUniqueId());
        if(player != null) {
            EntitiesPlugin.getEntityServer().getPlayerProvider().removePlayer(event.getPlayer());
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
