package com.mcmiddleearth.entities.events.listener;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.EntityAPI;
import com.mcmiddleearth.entities.entities.RealPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RealPlayer player = EntityAPI.getMcmePlayer(event.getPlayer().getUniqueId());
        if(player != null) {
            EntitiesPlugin.getEntityServer().getPlayerProvider().removePlayer(event.getPlayer());
        }
    }
}
