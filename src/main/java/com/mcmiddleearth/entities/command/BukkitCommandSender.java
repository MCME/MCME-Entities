package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.entities.entities.McmeEntity;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class BukkitCommandSender implements McmeCommandSender {

    CommandSender sender;

    Set<McmeEntity> selection = new HashSet<>();

    public BukkitCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(BaseComponent[] baseComponents) {
        sender.sendMessage(baseComponents);
    }

    public CommandSender getCommandSender() {
        return sender;
    }

    public Set<McmeEntity> getSelectedEntities() {
        return new HashSet<McmeEntity>(selection);
    }

    public void clearSelection() {
        selection.clear();
    }

    public void addToSelection(McmeEntity entity) {
        selection.add(entity);
    }

    public void removeFromSelection(McmeEntity entity) {
        selection.remove(entity);
    }

    public void setSelection(McmeEntity entity) {
        clearSelection();
        addToSelection(entity);
    }

}
