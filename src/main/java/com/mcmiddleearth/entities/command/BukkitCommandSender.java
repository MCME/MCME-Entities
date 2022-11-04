package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BukkitCommandSender implements McmeCommandSender {

    CommandSender sender;

    Set<McmeEntity> selectedEntities = new HashSet<>();

    McmeEntity selectedTargetEntity = null;

    List<Location> selectedPoints = new ArrayList<>();

    VirtualEntityFactory factory = new VirtualEntityFactory(new McmeEntityType(McmeEntityType.CustomEntityType.BAKED_ANIMATION),null);

    public BukkitCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(BaseComponent[] baseComponents) {
        sender.sendMessage(Component.text("[Entities] ", NamedTextColor.AQUA).append(Component.text(baseComponents[0].toLegacyText())));
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(Component.text("[Entities] ", NamedTextColor.AQUA).append(Component.text(message)));
    }

    @Override
    public void sendError(String message) {
        sender.sendMessage(Component.text("[Entities] ", NamedTextColor.AQUA).append(Component.text(message, NamedTextColor.RED)));
    }

    public CommandSender getCommandSender() {
        return sender;
    }

    public Set<McmeEntity> getSelectedEntities() {
        selectedEntities = selectedEntities.stream().filter(selectedTargetEntity -> !selectedTargetEntity.isTerminated())
                                                    .collect(Collectors.toSet());
        return new HashSet<>(selectedEntities);
    }

    public void clearSelectedEntities() {
        selectedEntities.clear();
    }

    public void setSelectedEntities(Set<McmeEntity> entities) { this.selectedEntities = entities; }

    public void addToSelectedEntities(McmeEntity entity) {
        selectedEntities.add(entity);
    }

    public void removeFromSelectedEntities(McmeEntity entity) {
        selectedEntities.remove(entity);
    }

    public void setSelectedEntities(McmeEntity entity) {
        clearSelectedEntities();
        addToSelectedEntities(entity);
    }

    public List<Location> getSelectedPoints() {
        return selectedPoints;
    }

    public void setSelectedPoints(List<Location> selectedPoints) {
        this.selectedPoints = selectedPoints;
    }

    public void setSelectedPoints(Location point) {
        selectedPoints.clear();
        selectedPoints.add(point);
    }
    public void clearSelectedPoints() {
        selectedPoints.clear();
    }

    public void addToSelectedPoints(Location  point) {
        selectedPoints.add(point);
    }

    public void removeFromSelectedPoints(Location point) {
        selectedPoints.remove(point);
    }

    public VirtualEntityFactory getEntityFactory() {
        return factory;
    }

    public void setEntityFactory(VirtualEntityFactory factory) {
        this.factory = factory;
    }

    public McmeEntity getSelectedTargetEntity() {
        return selectedTargetEntity;
    }

    public void setSelectedTargetEntity(McmeEntity target) {
        this.selectedTargetEntity = target;
    }
}
