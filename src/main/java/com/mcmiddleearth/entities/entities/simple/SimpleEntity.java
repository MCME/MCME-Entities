package com.mcmiddleearth.entities.entities.simple;

import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.*;
import com.mcmiddleearth.entities.protocol.packets.simple.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public abstract class SimpleEntity extends VirtualEntity {

    int entityId;

    protected AbstractPacket namePacket;
    protected AbstractPacket equipmentPacket;
    protected AbstractPacket metadataPacket;

    private MovementSpeed lastMovementSpeed;

    public SimpleEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        super(factory);
        this.entityId = entityId;
        teleportPacket = new SimpleEntityTeleportPacket(this);
        movePacket = new SimpleEntityMovePacket(this);
        removePacket = new VirtualEntityDestroyPacket(entityId);
        statusPacket = new SimpleEntityStatusPacket(entityId);
        namePacket = new DisplayNamePacket(entityId);
        equipmentPacket = new SimpleEntityEquipmentPacket(entityId);
        metadataPacket = new SimpleEntityMetadataPacket(entityId);
        if(this.getDisplayName()!=null) ((DisplayNamePacket)namePacket).setName(this.getDisplayName());
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public int getEntityQuantity() {
        return 1;
    }

    @Override
    public synchronized void addViewer(Player player) {
        super.addViewer(player);
//Logger.getGlobal().info("Send Display Name: "+ getDisplayName());
        if(player.hasPermission(Permission.VIEWER.getNode())) {
            if(getDisplayName()!=null) {
                namePacket.send(player);
            }
            equipmentPacket.send(player);
            metadataPacket.send(player);
        }
    }

    public void setDisplayName(String displayName) {
        super.setDisplayName(displayName);
        ((DisplayNamePacket)namePacket).setName(displayName);
        namePacket.send(getViewers());
    }

    @Override
    public void setEquipment(EquipmentSlot slot, ItemStack item) {
        ((SimpleEntityEquipmentPacket)equipmentPacket).setItem(slot,item);
        getViewers().forEach(viewer -> equipmentPacket.send(viewer));
    }

    @Override
    public void setMovementSpeed(MovementSpeed movementSpeed) {
        super.setMovementSpeed(movementSpeed);
        if (!movementSpeed.equals(lastMovementSpeed)) {
            ((SimpleEntityMetadataPacket) metadataPacket).setSprinting(movementSpeed.equals(MovementSpeed.SPRINT));
//Logger.getGlobal().info("Sprint: " + movementSpeed.equals(MovementSpeed.SPRINT));
            metadataPacket.update();
            metadataPacket.send(getViewers());
            lastMovementSpeed = movementSpeed;
        }
    }

}
