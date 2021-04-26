package com.mcmiddleearth.entities.events.listener;

import com.mcmiddleearth.entities.EntityAPI;
import com.mcmiddleearth.entities.events.events.McmeEntityRemoveEvent;
import com.mcmiddleearth.entities.events.events.VirtualPlayerAttackEvent;
import com.mcmiddleearth.entities.events.events.VirtualPlayerInteractEvent;
import com.mcmiddleearth.entities.events.handler.EntityEventHandler;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.inventory.EquipmentSlot;

public class EntitySelectionListener implements McmeEventListener {

    @EntityEventHandler
    public void onSelectionChange(VirtualPlayerInteractEvent event) {
        if(event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (event.isSneaking()) {
                event.getPlayer().removeFromSelection(event.getEntity());
                event.getPlayer().sendMessage(new ComponentBuilder("Removed from selection").create());
            } else {
                event.getPlayer().addToSelection(event.getEntity());
                event.getPlayer().sendMessage(new ComponentBuilder("Added to selection").create());
            }
        }
    }

    @EntityEventHandler
    public void onSelectionSet(VirtualPlayerAttackEvent event) {
        if (event.isSneaking()) {
            event.getPlayer().clearSelection();
            event.getPlayer().sendMessage(new ComponentBuilder("Selection cleared").create());
        } else {
            event.getPlayer().setSelection(event.getEntity());
            event.getPlayer().sendMessage(new ComponentBuilder("Selection set").create());
        }
    }

    @EntityEventHandler
    public void onEntityRemove(McmeEntityRemoveEvent event) {
        EntityAPI.getMcmePlayers().forEach(player -> player.getSelectedEntities().remove(event.getEntity()));
    }

}
