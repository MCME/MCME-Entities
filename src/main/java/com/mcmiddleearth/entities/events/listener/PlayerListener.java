package com.mcmiddleearth.entities.events.listener;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.EntityAPI;
import com.mcmiddleearth.entities.ai.goal.GoalDistance;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.events.events.player.VirtualPlayerAttackEvent;
import com.mcmiddleearth.entities.events.handler.EntityEventHandler;
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

    /*@EntityEventHandler
    public void attackVirtualEntity(VirtualPlayerAttackEvent event) {
        if(event.getPlayer().getLocation().distanceSquared(event.getEntity().getLocation()) < GoalDistance.ATTACK*1.5) {
            int damage = (int) (Math.random() * 8);
//Logger.getGlobal().info("Attack: " + event.getEntity().getType().getBukkitEntityType() + " " + damage);
            event.getEntity().receiveAttack(event.getPlayer(), damage, 1);
        }
    }*/

    /*@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        //manager.addPacketListener(new EntityListener(this));
        Logger.getGlobal().info("Manager: "+manager);
        manager.addPacketListener(new VirtualEntityUseListener(EntitiesPlugin.getInstance(), EntitiesPlugin.getEntityServer()));

    }*/
}
