package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class AddPassengerPacket extends AbstractPacket {

    private final PacketContainer add;

    public AddPassengerPacket(int entityId, int... passengerIds) {
        add = new PacketContainer(PacketType.Play.Server.MOUNT);
        add.getIntegers().write(0,entityId);
        add.getIntegerArrays().write(0,passengerIds);
    }

    @Override
    public void send(Player recipient) {
        send(add, recipient);
    }
}
