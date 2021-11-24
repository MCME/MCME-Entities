package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class VehicleMovePacket extends AbstractPacket {

    private final PacketContainer move;

    private final McmeEntity vehicle;

    public VehicleMovePacket(McmeEntity vehicle) {
        move = new PacketContainer(PacketType.Play.Server.MOUNT);
        this.vehicle = vehicle;
    }

    @Override
    public void update() {
        Location loc = vehicle.getLocation();
        move.getDoubles().write(0,loc.getX());
        move.getDoubles().write(1,loc.getY());
        move.getDoubles().write(2,loc.getZ());
        move.getFloat().write(0,loc.getYaw());
        move.getFloat().write(1,loc.getPitch());
    }

    @Override
    public void send(Player recipient) {
        send(move, recipient);
    }

}
