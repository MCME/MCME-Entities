package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class SimpleEntityStatusPacket extends AbstractPacket {

    private final PacketContainer status;

    public SimpleEntityStatusPacket(int entityId) {
        status = new PacketContainer(PacketType.Play.Server.ENTITY_STATUS);
        status.getIntegers().write(0,entityId);
    }

    public void setStatusCode(SimpleEntityStatusPacket.StatusCode statusCode) {
        status.getBytes().write(0, statusCode.getCode());
    }

    @Override
    public void send(Player recipient) {
        send(status,recipient);
    }

    public enum StatusCode {

        TIPPED_ARROW_PARTICLE   (0),
        ENTITY_HURT             (2),
        ENTITY_DEATH            (3);

        byte code;

        StatusCode(int code) {
            this.code = (byte) code;
        }

        public byte getCode() {
            return code;
        }
    }
}
