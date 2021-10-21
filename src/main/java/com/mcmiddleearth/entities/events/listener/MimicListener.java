package com.mcmiddleearth.entities.events.listener;

import com.mcmiddleearth.entities.api.ActionType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MimicListener implements Listener {

    VirtualEntity entity;
    McmeEntity mimic;

    public MimicListener(VirtualEntity entity, McmeEntity mimic) {
        this.entity = entity;
        this.mimic = mimic;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (mimic instanceof RealPlayer && ((RealPlayer) mimic).getBukkitPlayer().equals(event.getPlayer())) {
            if(event.getAction().equals(Action.LEFT_CLICK_AIR)
                    || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                entity.playAnimation(ActionType.ATTACK);
            } else if(event.getAction().equals(Action.RIGHT_CLICK_AIR)
                    || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                entity.playAnimation(ActionType.INTERACT);
            }
        }
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event) {

    }
}