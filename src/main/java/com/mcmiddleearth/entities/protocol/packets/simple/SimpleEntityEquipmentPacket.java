package com.mcmiddleearth.entities.protocol.packets.simple;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.mcmiddleearth.entities.protocol.packets.AbstractPacket;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SimpleEntityEquipmentPacket extends AbstractPacket {

    PacketContainer equipment;

    public SimpleEntityEquipmentPacket(int entityId) {
        equipment = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipment.getIntegers().write(0, entityId);
        setItem(EquipmentSlot.HAND, new ItemStack(Material.AIR));
    }

    public void setItem(EquipmentSlot slot, ItemStack item) {
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> inventory = new ArrayList<>();
        EnumWrappers.ItemSlot wrappedSlot;
        switch(slot) {
            case HEAD:
                wrappedSlot = EnumWrappers.ItemSlot.HEAD; break;
            case CHEST:
                wrappedSlot = EnumWrappers.ItemSlot.CHEST; break;
            case LEGS:
                wrappedSlot = EnumWrappers.ItemSlot.LEGS; break;
            case FEET:
                wrappedSlot = EnumWrappers.ItemSlot.FEET; break;
            case OFF_HAND:
                wrappedSlot = EnumWrappers.ItemSlot.OFFHAND; break;
            case HAND:
            default:
                wrappedSlot = EnumWrappers.ItemSlot.MAINHAND; break;
        }
        inventory.add(new Pair<>(wrappedSlot,item));
        equipment.getSlotStackPairLists().write(0, inventory);
    }

    @Override
    public void send(Player recipient) {
        send(equipment, recipient);
    }
}
